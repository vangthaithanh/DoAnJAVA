package com.example.webdulich.service;

import com.example.webdulich.entity.CustomItinerary;
import com.example.webdulich.entity.Property;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.CustomItineraryRepository;
import com.example.webdulich.repository.PropertyRepository;
import com.example.webdulich.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CustomItineraryService {

    private final CustomItineraryRepository customItineraryRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public CustomItineraryService(CustomItineraryRepository customItineraryRepository,
                                  UserRepository userRepository,
                                  PropertyRepository propertyRepository) {
        this.customItineraryRepository = customItineraryRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    public CustomItinerary create(
            Long userId,
            String destinationText,
            Integer totalDays,
            BigDecimal budget,
            String travelStyle,
            String note,
            String modelDestinationKey,
            String selectedPlaces,
            String selectedServices,
            Long selectedPropertyId,
            String selectedModelMaTour,
            String selectedTourTitle) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản hiện tại."));

        destinationText = normalize(destinationText);
        travelStyle = normalize(travelStyle);
        note = normalize(note);
        modelDestinationKey = normalize(modelDestinationKey);
        selectedPlaces = normalizeSelection(selectedPlaces);
        selectedServices = normalizeSelection(selectedServices);
        selectedModelMaTour = limit(normalize(selectedModelMaTour), 64);
        selectedTourTitle = limit(normalize(selectedTourTitle), 255);

        if (destinationText.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập điểm đến.");
        }

        if (totalDays == null || totalDays < 1) {
            throw new IllegalArgumentException("Số ngày phải từ 1 trở lên.");
        }

        if (budget != null && budget.signum() < 0) {
            throw new IllegalArgumentException("Ngân sách không được âm.");
        }

        Property selectedProperty = null;
        if (selectedPropertyId != null) {
            selectedProperty = propertyRepository.findById(selectedPropertyId)
                    .orElseThrow(() -> new IllegalArgumentException("Tour đã chọn không còn tồn tại trong hệ thống."));
            if (selectedTourTitle.isBlank()) {
                selectedTourTitle = selectedProperty.getTitle();
            }
            if (selectedModelMaTour.isBlank()) {
                selectedModelMaTour = normalize(selectedProperty.getModelMaTour());
            }
        }

        CustomItinerary itinerary = new CustomItinerary();
        itinerary.setUser(user);
        itinerary.setDestinationText(destinationText);
        itinerary.setTotalDays(totalDays);
        itinerary.setBudget(budget);
        itinerary.setTravelStyle(travelStyle.isBlank() ? "Chưa chọn" : travelStyle);
        itinerary.setNote(note);
        itinerary.setStatus("NEW");
        itinerary.setTitle(buildTitle(destinationText, totalDays));
        itinerary.setModelDestinationKey(modelDestinationKey);
        itinerary.setSelectedPlaces(selectedPlaces);
        itinerary.setSelectedServices(selectedServices);
        itinerary.setSelectedProperty(selectedProperty);
        itinerary.setSelectedModelMaTour(selectedModelMaTour);
        itinerary.setSelectedTourTitle(selectedTourTitle);

        return customItineraryRepository.save(itinerary);
    }

    public List<CustomItinerary> findByUser(Long userId) {
        return customItineraryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long countByUser(Long userId) {
        return customItineraryRepository.countByUserId(userId);
    }

    private String buildTitle(String destinationText, int totalDays) {
        String title = "Lịch trình " + destinationText + " " + totalDays + " ngày";
        return title.length() > 180 ? title.substring(0, 180) : title;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeSelection(String value) {
        value = normalize(value);
        return value.length() > 4000 ? value.substring(0, 4000) : value;
    }

    private String limit(String value, int maxLength) {
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
