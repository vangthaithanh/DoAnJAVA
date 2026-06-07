package com.example.webdulich.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_itineraries")
public class CustomItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Tên lịch trình không được để trống")
    @Column(nullable = false, length = 180)
    private String title;

    @NotBlank(message = "Điểm đến không được để trống")
    @Column(name = "destination_text", length = 255)
    private String destinationText;

    @NotNull(message = "Số ngày không được để trống")
    @Min(value = 1, message = "Số ngày phải từ 1 trở lên")
    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Column(name = "travel_style", length = 100)
    private String travelStyle;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(length = 30)
    private String status = "NEW";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private Agent assignedAgent;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "model_destination_key", length = 64)
    private String modelDestinationKey;

    @Column(name = "selected_places", columnDefinition = "TEXT")
    private String selectedPlaces;

    @Column(name = "selected_services", columnDefinition = "TEXT")
    private String selectedServices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_property_id")
    private Property selectedProperty;

    @Column(name = "selected_model_ma_tour", length = 64)
    private String selectedModelMaTour;

    @Column(name = "selected_tour_title", length = 255)
    private String selectedTourTitle;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforeCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null || status.isBlank()) {
            status = "NEW";
        }
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getTitle() { return title; }
    public String getDestinationText() { return destinationText; }
    public Integer getTotalDays() { return totalDays; }
    public BigDecimal getBudget() { return budget; }
    public String getTravelStyle() { return travelStyle; }
    public String getNote() { return note; }
    public String getStatus() { return status; }
    public Agent getAssignedAgent() { return assignedAgent; }
    public String getAdminNote() { return adminNote; }
    public String getModelDestinationKey() { return modelDestinationKey; }
    public String getSelectedPlaces() { return selectedPlaces; }
    public String getSelectedServices() { return selectedServices; }
    public Property getSelectedProperty() { return selectedProperty; }
    public String getSelectedModelMaTour() { return selectedModelMaTour; }
    public String getSelectedTourTitle() { return selectedTourTitle; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getSelectedPlacesDisplay() { return displayJsonArray(selectedPlaces); }
    public String getSelectedServicesDisplay() { return displayServiceArray(selectedServices); }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setTitle(String title) { this.title = title; }
    public void setDestinationText(String destinationText) { this.destinationText = destinationText; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public void setTravelStyle(String travelStyle) { this.travelStyle = travelStyle; }
    public void setNote(String note) { this.note = note; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignedAgent(Agent assignedAgent) { this.assignedAgent = assignedAgent; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public void setModelDestinationKey(String modelDestinationKey) { this.modelDestinationKey = modelDestinationKey; }
    public void setSelectedPlaces(String selectedPlaces) { this.selectedPlaces = selectedPlaces; }
    public void setSelectedServices(String selectedServices) { this.selectedServices = selectedServices; }
    public void setSelectedProperty(Property selectedProperty) { this.selectedProperty = selectedProperty; }
    public void setSelectedModelMaTour(String selectedModelMaTour) { this.selectedModelMaTour = selectedModelMaTour; }
    public void setSelectedTourTitle(String selectedTourTitle) { this.selectedTourTitle = selectedTourTitle; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    private String displayJsonArray(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String display = value.trim();
        if (display.startsWith("[") && display.endsWith("]")) {
            display = display.substring(1, display.length() - 1);
        }

        return display
                .replace("\\\"", "\"")
                .replace("\"", "")
                .replace(",", ", ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String displayServiceArray(String value) {
        String display = displayJsonArray(value);
        if (display.isBlank()) {
            return "";
        }

        String[] services = display.split("\\s*,\\s*");
        StringBuilder result = new StringBuilder();
        for (String service : services) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(serviceLabel(service));
        }
        return result.toString();
    }

    private String serviceLabel(String serviceKey) {
        return switch (serviceKey) {
            case "hotel" -> "Khách sạn";
            case "meal" -> "Bữa ăn";
            case "transport" -> "Xe du lịch";
            case "ticket" -> "Vé tham quan";
            case "guide" -> "Hướng dẫn viên";
            case "insurance" -> "Bảo hiểm du lịch";
            case "pickup" -> "Đưa đón";
            case "homestay" -> "Homestay";
            case "resort" -> "Resort";
            default -> serviceKey;
        };
    }
}
