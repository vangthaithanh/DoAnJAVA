package com.example.webdulich.recommendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/destinations")
    public List<Map<String, Object>> getDestinations() {
        return recommendationService.getDestinations();
    }

    @GetMapping("/places")
    public List<Map<String, Object>> getAvailablePlaces(
            @RequestParam(defaultValue = "da_lat") String destinationKey,
            @RequestParam(defaultValue = "false") boolean includeRare) {
        return recommendationService.getAvailablePlaces(destinationKey, includeRare);
    }

    @GetMapping("/services")
    public List<Map<String, Object>> getAvailableServices(
            @RequestParam(defaultValue = "da_lat") String destinationKey) {
        return recommendationService.getAvailableServices(destinationKey);
    }

    @PostMapping("/next-places")
    public Map<String, Object> recommendNextPlaces(@RequestBody RecommendationRequest request) {
        return recommendationService.recommendNextPlaces(
                request.getDestinationKey(), request.getSelectedPlaces(), request.getTopK());
    }

    @PostMapping("/next-services")
    public Map<String, Object> recommendNextServices(@RequestBody RecommendationRequest request) {
        return recommendationService.recommendNextServices(
                request.getDestinationKey(), request.getSelectedServices(), request.getTopK());
    }

    @PostMapping("/place-services")
    public Map<String, Object> recommendPlaceServices(@RequestBody RecommendationRequest request) {
        return recommendationService.recommendPlaceServices(
                request.getDestinationKey(),
                request.getSelectedPlaces(),
                request.getSelectedServices(),
                request.getTopK());
    }

    @PostMapping("/tours")
    public Map<String, Object> recommendTours(@RequestBody RecommendationRequest request) {
        return recommendationService.recommendTours(
                request.getDestinationKey(),
                request.getSelectedPlaces(),
                request.getSelectedServices(),
                request.getTopK());
    }

    @PostMapping("/full")
    public Map<String, Object> recommendFull(@RequestBody RecommendationRequest request) {
        return recommendationService.recommendFull(
                request.getDestinationKey(),
                request.getSelectedPlaces(),
                request.getSelectedServices(),
                request.getTopK());
    }
}
