package com.example.webdulich.recommendation;

import com.example.webdulich.entity.Property;
import com.example.webdulich.repository.PropertyRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final String MODEL_PATH = "recommendation_model_v11/";
    private static final String DEFAULT_DESTINATION = "da_lat";
    private static final int MAX_TOP_K = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationService.class);

    private final ObjectMapper objectMapper;
    private final PropertyRepository propertyRepository;

    private List<DestinationInfo> destinations = List.of();
    private Map<String, DestinationModel> models = Map.of();

    public RecommendationService(ObjectMapper objectMapper, PropertyRepository propertyRepository) {
        this.objectMapper = objectMapper;
        this.propertyRepository = propertyRepository;
    }

    @PostConstruct
    public void loadModel() {
        try {
            readJson("metadata_global.json");
            List<DestinationInfo> loadedDestinations = loadDestinations(readJson("destinations.json"));
            Map<String, DestinationModel> loadedModels = new LinkedHashMap<>();
            for (DestinationInfo destination : loadedDestinations) {
                loadedModels.put(destination.key(), loadDestinationModel(destination));
            }
            destinations = List.copyOf(loadedDestinations);
            models = Map.copyOf(loadedModels);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot load recommendation model v11 from classpath", exception);
        }
    }

    public List<Map<String, Object>> getDestinations() {
        return destinations.stream().map(DestinationInfo::toMap).toList();
    }

    public List<Map<String, Object>> getAvailablePlaces(String destinationKey, boolean includeRare) {
        DestinationModel model = requireModel(destinationKey);
        return model.places().stream()
                .filter(place -> includeRare || place.isCore())
                .map(Place::toMap)
                .toList();
    }

    public List<Map<String, Object>> getAvailableServices(String destinationKey) {
        return requireModel(destinationKey).services().stream()
                .map(ServiceInfo::toMap)
                .toList();
    }

    public Map<String, Object> recommendNextPlaces(String destinationKey, List<String> selectedPlaces, int topK) {
        DestinationModel model = requireModel(destinationKey);
        List<String> selected = normalize(selectedPlaces);
        List<String> known = selected.stream().filter(model.placesByName()::containsKey).toList();
        List<String> unknown = selected.stream().filter(place -> !model.placesByName().containsKey(place)).toList();
        List<Map<String, Object>> recommendations;
        if (known.size() == 1) {
            recommendations = recommendNextPlacesFromRules(model, known.get(0), selected, topK);
            if (recommendations.isEmpty()) {
                recommendations = recommendNextPlacesFromTransactions(model, known, selected, topK);
            }
        } else {
            recommendations = recommendNextPlacesFromTransactions(model, known, selected, topK);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", recommendations.isEmpty() ? "insufficient_rules" : "ok");
        response.put("selectedPlaces", selected);
        response.put("unknownPlaces", unknown);
        response.put("recommendations", recommendations);
        return response;
    }

    public Map<String, Object> recommendNextServices(
            String destinationKey, List<String> selectedServices, int topK) {
        DestinationModel model = requireModel(destinationKey);
        List<String> selected = normalize(selectedServices);
        List<String> known = selected.stream().filter(model.servicesByKey()::containsKey).toList();
        List<String> unknown = selected.stream()
                .filter(service -> !model.servicesByKey().containsKey(service))
                .toList();
        LinkedHashMap<String, Map<String, Object>> recommendations = new LinkedHashMap<>();
        appendServiceRules(model, known, selected, "manh", recommendations);
        appendServiceRules(model, known, selected, "tham_khao", recommendations);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", recommendations.isEmpty() ? "insufficient_rules" : "ok");
        response.put("selectedServices", selected);
        response.put("unknownServices", unknown);
        response.put("recommendations", recommendations.values().stream().limit(sanitizeTopK(topK)).toList());
        return response;
    }

    public Map<String, Object> recommendPlaceServices(
            String destinationKey, List<String> selectedPlaces, List<String> selectedServices, int topK) {
        DestinationModel model = requireModel(destinationKey);
        List<String> selected = normalize(selectedPlaces);
        List<String> services = normalize(selectedServices);
        List<String> known = selected.stream().filter(model.placesByName()::containsKey).toList();
        List<String> unknown = selected.stream().filter(place -> !model.placesByName().containsKey(place)).toList();
        Map<String, PlaceServiceAggregate> aggregates = new LinkedHashMap<>();
        model.placeServiceStats().stream()
                .filter(stat -> known.contains(stat.place()))
                .filter(stat -> !services.contains(stat.serviceKey()))
                .forEach(stat -> aggregates.computeIfAbsent(
                                stat.serviceKey(),
                                service -> new PlaceServiceAggregate(model.servicesByKey().get(service)))
                        .add(stat));

        List<Map<String, Object>> recommendations = aggregates.values().stream()
                .sorted(Comparator.comparingInt(PlaceServiceAggregate::confidenceLevelRank).reversed()
                        .thenComparing(Comparator.comparingDouble(PlaceServiceAggregate::serviceConfidence).reversed())
                        .thenComparing(Comparator.comparingDouble(PlaceServiceAggregate::serviceCoverageRate).reversed())
                        .thenComparingInt(aggregate -> model.serviceOrder().getOrDefault(
                                aggregate.serviceKey(), Integer.MAX_VALUE)))
                .limit(sanitizeTopK(topK))
                .map(PlaceServiceAggregate::toMap)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", recommendations.isEmpty() ? "insufficient_stats" : "ok");
        response.put("selectedPlaces", selected);
        response.put("selectedServices", services);
        response.put("unknownPlaces", unknown);
        response.put("recommendations", recommendations);
        return response;
    }

    public Map<String, Object> recommendTours(
            String destinationKey, List<String> selectedPlaces, List<String> selectedServices, int topK) {
        DestinationModel model = requireModel(destinationKey);
        List<String> places = normalize(selectedPlaces);
        List<String> services = normalize(selectedServices);
        List<String> unknownPlaces = places.stream().filter(place -> !model.placesByName().containsKey(place)).toList();
        List<String> unknownServices = services.stream()
                .filter(service -> !model.servicesByKey().containsKey(service))
                .toList();

        List<TourRecommendation> recommendations = model.tours().stream()
                .map(tour -> createTourRecommendation(model, tour, places, services))
                .filter(TourRecommendation::isExactMatch)
                .sorted(Comparator.comparingDouble(TourRecommendation::score).reversed()
                        .thenComparing(TourRecommendation::tourId))
                .limit(sanitizeTopK(topK))
                .toList();
        Map<String, Property> propertiesByModelTour = findPropertiesByModelTour(
                recommendations.stream().map(TourRecommendation::tourId).toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", recommendations.isEmpty() ? "no_exact_match" : "ok");
        response.put("unknownPlaces", unknownPlaces);
        response.put("unknownServices", unknownServices);
        response.put("tours", recommendations.stream()
                .map(recommendation -> recommendation.toMap(propertiesByModelTour.get(recommendation.tourId())))
                .toList());
        return response;
    }

    public Map<String, Object> recommendFull(
            String destinationKey, List<String> selectedPlaces, List<String> selectedServices, int topK) {
        DestinationModel model = requireModel(destinationKey);
        List<String> places = normalize(selectedPlaces);
        List<String> services = normalize(selectedServices);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("destination", model.destination().summary());
        response.put("selectedPlaces", places);
        response.put("selectedServices", services);
        response.put("nextPlaces", recommendNextPlaces(model.destination().key(), places, topK));
        response.put("nextServices", recommendNextServices(model.destination().key(), services, topK));
        response.put("placeServices", recommendPlaceServices(model.destination().key(), places, services, topK));
        response.put("recommendedTours", recommendTours(model.destination().key(), places, services, topK));
        response.put("warnings", readinessWarnings(model));
        response.put("status", "ok");
        response.put("modelReadiness", model.readiness());
        return response;
    }

    private List<Map<String, Object>> recommendNextPlacesFromRules(
            DestinationModel model, String antecedent, List<String> selected, int topK) {
        LinkedHashMap<String, Map<String, Object>> recommendations = new LinkedHashMap<>();
        appendPlaceRules(model, antecedent, selected, "manh", false, recommendations);
        appendPlaceRules(model, antecedent, selected, "tham_khao", false, recommendations);
        if (recommendations.isEmpty()) {
            appendPlaceRules(model, antecedent, selected, "manh", true, recommendations);
            appendPlaceRules(model, antecedent, selected, "tham_khao", true, recommendations);
        }
        return recommendations.values().stream().limit(sanitizeTopK(topK)).toList();
    }

    private List<Map<String, Object>> recommendNextPlacesFromTransactions(
            DestinationModel model, List<String> known, List<String> selected, int topK) {
        if (known.isEmpty()) {
            return List.of();
        }
        List<PlaceTransaction> matches = model.placeTransactions().stream()
                .filter(transaction -> transaction.items().containsAll(known))
                .toList();
        List<PlaceTransaction> dayMatches = matches.stream()
                .filter(transaction -> "day_level".equals(transaction.type()))
                .toList();
        List<PlaceTransaction> preferredMatches = dayMatches.isEmpty()
                ? matches.stream().filter(transaction -> "tour_level".equals(transaction.type())).toList()
                : dayMatches;
        if (preferredMatches.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> coreCandidateCounts = new LinkedHashMap<>();
        Map<String, Integer> rareCandidateCounts = new LinkedHashMap<>();
        preferredMatches.forEach(transaction -> transaction.items().stream()
                .filter(place -> !selected.contains(place))
                .forEach(place -> {
                    Map<String, Integer> counts = model.placesByName()
                            .getOrDefault(place, Place.UNKNOWN)
                            .isCore() ? coreCandidateCounts : rareCandidateCounts;
                    counts.merge(place, 1, Integer::sum);
                }));
        String transactionType = preferredMatches.get(0).type();
        List<Map<String, Object>> recommendations = new ArrayList<>();
        appendTransactionCandidates(recommendations, coreCandidateCounts, preferredMatches.size(), transactionType, "core");
        if (recommendations.isEmpty()) {
            appendTransactionCandidates(recommendations, rareCandidateCounts, preferredMatches.size(), transactionType, "rare");
        }
        return recommendations.stream()
                .limit(sanitizeTopK(topK))
                .toList();
    }

    private void appendTransactionCandidates(
            List<Map<String, Object>> recommendations,
            Map<String, Integer> candidateCounts,
            int matchCount,
            String transactionType,
            String placeLevel) {
        candidateCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .forEach(entry -> {
                    Map<String, Object> value = new LinkedHashMap<>();
                    value.put("consequent", entry.getKey());
                    value.put("support_count", entry.getValue());
                    value.put("confidence", round((double) entry.getValue() / matchCount));
                    value.put("transaction_type", transactionType);
                    value.put("place_level", placeLevel);
                    value.put("recommendation_level", "rare".equals(placeLevel) ? "tham_khao" : "manh");
                    recommendations.add(value);
                });
    }

    private void appendPlaceRules(
            DestinationModel model,
            String antecedent,
            List<String> selected,
            String level,
            boolean rareOnly,
            LinkedHashMap<String, Map<String, Object>> recommendations) {
        model.placeRules().getOrDefault(antecedent, List.of()).stream()
                .filter(rule -> level.equals(rule.level()))
                .filter(rule -> !selected.contains(rule.consequent()))
                .filter(rule -> rareOnly != model.placesByName()
                        .getOrDefault(rule.consequent(), Place.UNKNOWN)
                        .isCore())
                .forEach(rule -> {
                    Map<String, Object> value = new LinkedHashMap<>(rule.payload());
                    value.put("place_level", rareOnly ? "rare" : "core");
                    value.put("recommendation_level", rule.level());
                    recommendations.putIfAbsent(rule.consequent(), value);
                });
    }

    private void appendServiceRules(
            DestinationModel model,
            List<String> known,
            List<String> selected,
            String level,
            LinkedHashMap<String, Map<String, Object>> recommendations) {
        known.forEach(service -> model.serviceRules().getOrDefault(service, List.of()).stream()
                .filter(rule -> level.equals(rule.level()))
                .filter(rule -> !selected.contains(rule.consequent()))
                .forEach(rule -> {
                    Map<String, Object> value = new LinkedHashMap<>(rule.payload());
                    value.put("recommendation_level", rule.level());
                    recommendations.putIfAbsent(rule.consequent(), value);
                }));
    }

    private TourRecommendation createTourRecommendation(
            DestinationModel model, Tour tour, List<String> selectedPlaces, List<String> selectedServices) {
        List<String> matchedPlaces = selectedPlaces.stream().filter(tour.places()::contains).toList();
        List<String> missingPlaces = selectedPlaces.stream().filter(place -> !tour.places().contains(place)).toList();
        Map<String, Double> tourServices = model.servicesByTour().getOrDefault(tour.id(), Map.of());
        List<String> matchedServices = selectedServices.stream()
                .filter(service -> tourServices.get(service) != null && tourServices.get(service) > 0)
                .toList();
        List<String> missingServices = selectedServices.stream()
                .filter(service -> !matchedServices.contains(service))
                .toList();
        double placeCoverage = coverage(matchedPlaces.size(), selectedPlaces.size());
        double serviceCoverage = coverage(matchedServices.size(), selectedServices.size());
        double serviceQuality = switch (tour.serviceDataQuality()) {
            case "cao" -> 1.0;
            case "trung_binh" -> 0.7;
            default -> 0.4;
        };
        double priceDurationAvailable = tour.payload().get("gia_tu") != null
                && tour.payload().get("so_ngay") != null ? 1.0 : 0.0;
        double score = 0.40 * placeCoverage
                + 0.20 * serviceCoverage
                + 0.15 * tour.quality()
                + 0.10 * serviceQuality
                + 0.10 * placeCoverage
                + 0.05 * priceDurationAvailable;
        return new TourRecommendation(
                tour,
                matchedPlaces,
                missingPlaces,
                matchedServices,
                missingServices,
                round(score));
    }

    private Map<String, Property> findPropertiesByModelTour(List<String> modelTourIds) {
        if (modelTourIds.isEmpty()) {
            return Map.of();
        }
        try {
            return propertyRepository.findByModelMaTourIn(modelTourIds).stream()
                    .collect(Collectors.toMap(Property::getModelMaTour, Function.identity()));
        } catch (RuntimeException exception) {
            LOGGER.warn("Cannot enrich model tours from properties; returning model-only recommendations", exception);
            return Map.of();
        }
    }

    private List<String> readinessWarnings(DestinationModel model) {
        if ("ready".equals(model.readiness())) {
            return List.of();
        }
        if ("not_ready".equals(model.readiness())) {
            return List.of("Dữ liệu điểm đến này còn ít, kết quả chỉ mang tính tham khảo.");
        }
        return List.of("Dữ liệu điểm đến này còn hạn chế, vui lòng xem kết quả như thông tin tham khảo.");
    }

    private DestinationModel requireModel(String destinationKey) {
        String key = destinationKey == null || destinationKey.isBlank()
                ? DEFAULT_DESTINATION
                : destinationKey.trim();
        DestinationModel model = models.get(key);
        if (model == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown destinationKey: " + key);
        }
        return model;
    }

    private DestinationModel loadDestinationModel(DestinationInfo destination) throws IOException {
        String path = destination.key() + "/";
        readJson(path + "tour_recommendation_index.json");
        JsonNode metadata = readJson(path + "metadata.json");
        List<Place> places = loadPlaces(readJson(path + "places.json"));
        List<ServiceInfo> services = loadServices(readJson(path + "services.json"));
        return new DestinationModel(
                destination,
                metadata.path("model_readiness").asText(destination.readiness()),
                metadata.path("reason_if_limited_or_not_ready").isNull()
                        ? null
                        : metadata.path("reason_if_limited_or_not_ready").asText(),
                places,
                places.stream().collect(Collectors.toMap(Place::name, Function.identity())),
                loadPlaceRules(readJson(path + "place_rules.json")),
                loadServiceRules(readJson(path + "service_rules.json")),
                services,
                services.stream().collect(Collectors.toMap(ServiceInfo::key, Function.identity())),
                serviceOrder(services),
                loadPlaceServiceStats(readJson(path + "place_to_service_stats.json")),
                loadPlaceTransactions(readJson(path + "transactions_places.json")),
                loadServicesByTour(readJson(path + "transactions_services.json")),
                loadTours(readJson(path + "tours.json")));
    }

    private List<DestinationInfo> loadDestinations(JsonNode root) {
        List<DestinationInfo> values = new ArrayList<>();
        root.forEach(node -> values.add(new DestinationInfo(
                node.path("destination_key").asText(),
                node.path("destination_name").asText(),
                node.path("clean_tour_count").asInt(),
                node.path("place_count").asInt(),
                node.path("service_count").asInt(),
                node.path("model_readiness").asText())));
        return values;
    }

    private List<Place> loadPlaces(JsonNode root) {
        List<Place> values = new ArrayList<>();
        root.forEach(node -> values.add(new Place(
                node.path("name").asText(),
                node.path("destination_key").asText(),
                node.path("tour_count").asInt(),
                node.path("place_level").asText())));
        return List.copyOf(values);
    }

    private List<ServiceInfo> loadServices(JsonNode root) {
        List<ServiceInfo> values = new ArrayList<>();
        root.forEach(node -> values.add(new ServiceInfo(
                node.path("service_key").asText(),
                node.path("service_label").asText(),
                node.path("known_tour_count").asInt(),
                node.path("available_tour_count").asInt())));
        return List.copyOf(values);
    }

    private Map<String, List<PlaceRule>> loadPlaceRules(JsonNode root) {
        Map<String, List<PlaceRule>> values = new LinkedHashMap<>();
        root.fields().forEachRemaining(entry -> {
            List<PlaceRule> rules = new ArrayList<>();
            entry.getValue().forEach(node -> rules.add(new PlaceRule(
                    node.path("consequent").asText(),
                    node.path("rule_level").asText(),
                    payload(node))));
            values.put(entry.getKey(), List.copyOf(rules));
        });
        return Map.copyOf(values);
    }

    private Map<String, List<ServiceRule>> loadServiceRules(JsonNode root) {
        Map<String, List<ServiceRule>> values = new LinkedHashMap<>();
        root.fields().forEachRemaining(entry -> {
            List<ServiceRule> rules = new ArrayList<>();
            entry.getValue().forEach(node -> rules.add(new ServiceRule(
                    node.path("consequent_service").asText(),
                    node.path("rule_level").asText(),
                    payload(node))));
            values.put(entry.getKey(), List.copyOf(rules));
        });
        return Map.copyOf(values);
    }

    private List<PlaceServiceStat> loadPlaceServiceStats(JsonNode root) {
        List<PlaceServiceStat> values = new ArrayList<>();
        root.forEach(node -> values.add(new PlaceServiceStat(
                node.path("place").asText(),
                node.path("service_key").asText(),
                node.path("matched_tour_count").asInt(),
                node.path("service_available_count").asInt(),
                node.path("service_known_count").asInt(),
                node.path("service_coverage_rate").asDouble(),
                node.path("service_confidence").asDouble())));
        return List.copyOf(values);
    }

    private List<PlaceTransaction> loadPlaceTransactions(JsonNode root) {
        List<PlaceTransaction> values = new ArrayList<>();
        root.fields().forEachRemaining(entry -> values.add(new PlaceTransaction(
                entry.getValue().path("transaction_type").asText(),
                textList(entry.getValue().path("items")))));
        return List.copyOf(values);
    }

    private Map<String, Map<String, Double>> loadServicesByTour(JsonNode root) {
        Map<String, Map<String, Double>> values = new LinkedHashMap<>();
        root.fields().forEachRemaining(entry -> {
            JsonNode transaction = entry.getValue();
            Map<String, Double> services = new LinkedHashMap<>();
            transaction.path("services").fields().forEachRemaining(service ->
                    services.put(service.getKey(), service.getValue().isNull() ? null : service.getValue().asDouble()));
            values.put(transaction.path("ma_tour").asText(), Collections.unmodifiableMap(services));
        });
        return Map.copyOf(values);
    }

    private List<Tour> loadTours(JsonNode root) {
        List<Tour> values = new ArrayList<>();
        root.forEach(node -> values.add(new Tour(
                node.path("ma_tour").asText(),
                textList(node.path("places")),
                node.path("tour_quality_score").asDouble(),
                node.path("service_data_quality").asText(),
                payload(node))));
        return List.copyOf(values);
    }

    private Map<String, Integer> serviceOrder(List<ServiceInfo> services) {
        Map<String, Integer> values = new LinkedHashMap<>();
        for (int index = 0; index < services.size(); index++) {
            values.put(services.get(index).key(), index);
        }
        return Map.copyOf(values);
    }

    private Map<String, Object> payload(JsonNode node) {
        return objectMapper.convertValue(node, new TypeReference<LinkedHashMap<String, Object>>() { });
    }

    private JsonNode readJson(String filename) throws IOException {
        try (InputStream inputStream = new ClassPathResource(MODEL_PATH + filename).getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }

    private List<String> textList(JsonNode root) {
        List<String> values = new ArrayList<>();
        root.forEach(node -> values.add(node.asText()));
        return List.copyOf(values);
    }

    private List<String> normalize(List<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

    private int sanitizeTopK(int topK) {
        return Math.max(1, Math.min(topK, MAX_TOP_K));
    }

    private double coverage(int matched, int selected) {
        return selected == 0 ? 1.0 : (double) matched / selected;
    }

    private static double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }

    private record DestinationInfo(
            String key, String name, int cleanTourCount, int placeCount, int serviceCount, String readiness) {

        private Map<String, Object> toMap() {
            Map<String, Object> value = summary();
            value.put("cleanTourCount", cleanTourCount);
            value.put("placeCount", placeCount);
            value.put("serviceCount", serviceCount);
            value.put("modelReadiness", readiness);
            return value;
        }

        private Map<String, Object> summary() {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("destinationKey", key);
            value.put("destinationName", name);
            return value;
        }
    }

    private record DestinationModel(
            DestinationInfo destination,
            String readiness,
            String readinessReason,
            List<Place> places,
            Map<String, Place> placesByName,
            Map<String, List<PlaceRule>> placeRules,
            Map<String, List<ServiceRule>> serviceRules,
            List<ServiceInfo> services,
            Map<String, ServiceInfo> servicesByKey,
            Map<String, Integer> serviceOrder,
            List<PlaceServiceStat> placeServiceStats,
            List<PlaceTransaction> placeTransactions,
            Map<String, Map<String, Double>> servicesByTour,
            List<Tour> tours) {
    }

    private record Place(String name, String destinationKey, int tourCount, String level) {

        private static final Place UNKNOWN = new Place("", "", 0, "rare");

        private boolean isCore() {
            return "core".equals(level);
        }

        private Map<String, Object> toMap() {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("name", name);
            value.put("destinationKey", destinationKey);
            value.put("tourCount", tourCount);
            value.put("placeLevel", level);
            return value;
        }
    }

    private record ServiceInfo(String key, String label, int knownTourCount, int availableTourCount) {

        private Map<String, Object> toMap() {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("serviceKey", key);
            value.put("serviceLabel", label);
            value.put("knownTourCount", knownTourCount);
            value.put("availableTourCount", availableTourCount);
            return value;
        }
    }

    private record PlaceRule(String consequent, String level, Map<String, Object> payload) {
    }

    private record ServiceRule(String consequent, String level, Map<String, Object> payload) {
    }

    private record PlaceServiceStat(
            String place,
            String serviceKey,
            int matchedTourCount,
            int availableCount,
            int knownCount,
            double coverageRate,
            double confidence) {
    }

    private record PlaceTransaction(String type, List<String> items) {
    }

    private record Tour(
            String id, List<String> places, double quality, String serviceDataQuality, Map<String, Object> payload) {
    }

    private record TourRecommendation(
            Tour tour,
            List<String> matchedPlaces,
            List<String> missingPlaces,
            List<String> matchedServices,
            List<String> missingServices,
            double score) {

        private String tourId() {
            return tour.id();
        }

        private boolean isExactMatch() {
            return missingPlaces.isEmpty() && missingServices.isEmpty();
        }

        private Map<String, Object> toMap(Property property) {
            Map<String, Object> value = new LinkedHashMap<>();
            for (String key : List.of(
                    "ma_tour", "tieu_de", "nguon", "url", "destination_key",
                    "gia_tu", "so_ngay", "so_dem", "noi_khoi_hanh")) {
                value.put(key, tour.payload().get(key));
            }
            value.put("matchedPlaces", matchedPlaces);
            value.put("missingPlaces", missingPlaces);
            value.put("matchedServices", matchedServices);
            value.put("missingServices", missingServices);
            value.put("tourScore", score);
            value.put("recommendationReason",
                    missingPlaces.isEmpty() && missingServices.isEmpty() ? "full_match" : "partial_match");
            value.put("serviceDataQuality", tour.serviceDataQuality());
            if (property != null) {
                value.put("tieu_de", property.getTitle());
                value.put("nguon", property.getModelSource());
                value.put("url", property.getModelUrl());
                value.put("destination_key", property.getModelDestinationKey());
                value.put("gia_tu", property.getPrice());
                value.put("so_ngay", property.getBedrooms());
                value.put("so_dem", property.getBathrooms());
                value.put("propertyId", property.getId());
                value.put("detailUrl", "/tours/" + property.getId());
                value.put("bookable", true);
            } else {
                value.put("detailUrl", tour.payload().get("url"));
                value.put("bookable", false);
            }
            return value;
        }
    }

    private static final class PlaceServiceAggregate {

        private final ServiceInfo service;
        private int matchedPlaceCount;
        private int matchedTourCount = Integer.MAX_VALUE;
        private int availableCount = Integer.MAX_VALUE;
        private int knownCount = Integer.MAX_VALUE;
        private double coverageRateTotal;
        private double confidenceTotal;

        private PlaceServiceAggregate(ServiceInfo service) {
            this.service = service;
        }

        private void add(PlaceServiceStat stat) {
            matchedPlaceCount++;
            matchedTourCount = Math.min(matchedTourCount, stat.matchedTourCount());
            availableCount = Math.min(availableCount, stat.availableCount());
            knownCount = Math.min(knownCount, stat.knownCount());
            coverageRateTotal += stat.coverageRate();
            confidenceTotal += stat.confidence();
        }

        private String serviceKey() {
            return service.key();
        }

        private double serviceCoverageRate() {
            return matchedPlaceCount == 0 ? 0 : round(coverageRateTotal / matchedPlaceCount);
        }

        private double serviceConfidence() {
            return matchedPlaceCount == 0 ? 0 : round(confidenceTotal / matchedPlaceCount);
        }

        private int confidenceLevelRank() {
            return switch (confidenceLevel()) {
                case "cao" -> 2;
                case "trung_binh" -> 1;
                default -> 0;
            };
        }

        private String confidenceLevel() {
            if (serviceCoverageRate() >= 0.8) {
                return "cao";
            }
            if (serviceCoverageRate() >= 0.5) {
                return "trung_binh";
            }
            return "thap";
        }

        private Map<String, Object> toMap() {
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("service_key", service.key());
            value.put("service_label", service.label());
            value.put("matched_place_count", matchedPlaceCount);
            value.put("matched_tour_count", matchedTourCount == Integer.MAX_VALUE ? 0 : matchedTourCount);
            value.put("service_available_count", availableCount == Integer.MAX_VALUE ? 0 : availableCount);
            value.put("service_known_count", knownCount == Integer.MAX_VALUE ? 0 : knownCount);
            value.put("service_coverage_rate", serviceCoverageRate());
            value.put("service_confidence", serviceConfidence());
            value.put("confidence_level", confidenceLevel());
            return value;
        }
    }
}
