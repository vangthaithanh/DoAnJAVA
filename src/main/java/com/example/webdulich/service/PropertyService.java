package com.example.webdulich.service;

import com.example.webdulich.entity.Property;
import com.example.webdulich.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<Property> search(String keyword, String type, String location, String status,
                                 BigDecimal minPrice, BigDecimal maxPrice) {
        return propertyRepository.searchProperties(keyword, type, location, status, minPrice, maxPrice);
    }

    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    public List<Property> findLatestSix() {
        return propertyRepository.findTop6ByOrderByIdDesc();
    }

    public List<Property> findLatestThree() {
        return propertyRepository.findTop3ByOrderByIdDesc();
    }

    public List<Property> findByAgent(Long agentId) {
        return propertyRepository.findByAgentIdOrderByIdDesc(agentId);
    }

    public Optional<Property> findById(Long id) {
        return propertyRepository.findById(id);
    }

    public Property save(Property property) {
        if (property.getStatus() == null || property.getStatus().isBlank()) {
            property.setStatus("Đang mở bán");
        }

        if (property.getImageUrl() == null || property.getImageUrl().isBlank()) {
            property.setImageUrl("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80");
        }

        if (property.getGalleryImageOne() == null || property.getGalleryImageOne().isBlank()) {
            property.setGalleryImageOne(property.getImageUrl());
        }

        if (property.getGalleryImageTwo() == null || property.getGalleryImageTwo().isBlank()) {
            property.setGalleryImageTwo(property.getImageUrl());
        }

        if (property.getGalleryImageThree() == null || property.getGalleryImageThree().isBlank()) {
            property.setGalleryImageThree(property.getImageUrl());
        }

        if (property.getFeatured() == null) {
            property.setFeatured(false);
        }

        return propertyRepository.save(property);
    }

    public void deleteById(Long id) {
        propertyRepository.deleteById(id);
    }

    public Property getFeaturedProperty() {
        return propertyRepository.findFirstByFeaturedTrueOrderByIdDesc()
                .orElseGet(() -> propertyRepository.findAll().stream().findFirst().orElse(null));
    }

    public long countByType(String type) {
        return propertyRepository.countByTypeIgnoreCase(type);
    }

    public long countByCity(String city) {
        return propertyRepository.countByCityIgnoreCase(city);
    }
}
