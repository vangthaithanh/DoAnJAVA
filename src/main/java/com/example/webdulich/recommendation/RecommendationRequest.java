package com.example.webdulich.recommendation;

import java.util.List;

public class RecommendationRequest {

    private String destinationKey = "da_lat";
    private List<String> selectedPlaces = List.of();
    private List<String> selectedServices = List.of();
    private Integer topK = 5;

    public String getDestinationKey() {
        return destinationKey == null || destinationKey.isBlank() ? "da_lat" : destinationKey.trim();
    }

    public void setDestinationKey(String destinationKey) {
        this.destinationKey = destinationKey;
    }

    public List<String> getSelectedPlaces() {
        return selectedPlaces == null ? List.of() : selectedPlaces;
    }

    public void setSelectedPlaces(List<String> selectedPlaces) {
        this.selectedPlaces = selectedPlaces;
    }

    public List<String> getSelectedServices() {
        return selectedServices == null ? List.of() : selectedServices;
    }

    public void setSelectedServices(List<String> selectedServices) {
        this.selectedServices = selectedServices;
    }

    public int getTopK() {
        return topK == null ? 5 : topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }
}
