package com.example.webdulich.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên bất động sản không được để trống")
    @Column(nullable = false, length = 150)
    private String title;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá phải lớn hơn hoặc bằng 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String galleryImageOne;

    @Column(length = 500)
    private String galleryImageTwo;

    @Column(length = 500)
    private String galleryImageThree;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(nullable = false, length = 255)
    private String location;

    @Column(length = 50)
    private String status;

    @Column(length = 80)
    private String type;

    @Column(length = 80)
    private String city;

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer parking;
    private Integer area;
    private Integer yearBuilt;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean featured = false;

    @Column(name = "model_ma_tour", length = 64)
    private String modelMaTour;

    @Column(name = "model_source", length = 80)
    private String modelSource;

    @Column(name = "model_url", length = 500)
    private String modelUrl;

    @Column(name = "recommendation_enabled", nullable = false)
    private Boolean recommendationEnabled = false;

    @Column(name = "model_destination_key", length = 64)
    private String modelDestinationKey;

    @Column(name = "model_version", length = 20)
    private String modelVersion;

    @Column(name = "model_places", columnDefinition = "JSON")
    private String modelPlaces;

    @Column(name = "model_services", columnDefinition = "JSON")
    private String modelServices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    public Property() {
    }

    public Property(String title, BigDecimal price, String imageUrl, String galleryImageOne, String galleryImageTwo,
                    String galleryImageThree, String location, String status, String type, String city,
                    Integer bedrooms, Integer bathrooms, Integer parking, Integer area, Integer yearBuilt,
                    String description, Boolean featured, Agent agent) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.galleryImageOne = galleryImageOne;
        this.galleryImageTwo = galleryImageTwo;
        this.galleryImageThree = galleryImageThree;
        this.location = location;
        this.status = status;
        this.type = type;
        this.city = city;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.parking = parking;
        this.area = area;
        this.yearBuilt = yearBuilt;
        this.description = description;
        this.featured = featured;
        this.agent = agent;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getGalleryImageOne() { return galleryImageOne; }
    public String getGalleryImageTwo() { return galleryImageTwo; }
    public String getGalleryImageThree() { return galleryImageThree; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getCity() { return city; }
    public Integer getBedrooms() { return bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public Integer getParking() { return parking; }
    public Integer getArea() { return area; }
    public Integer getYearBuilt() { return yearBuilt; }
    public String getDescription() { return description; }
    public Boolean getFeatured() { return featured; }
    public String getModelMaTour() { return modelMaTour; }
    public String getModelSource() { return modelSource; }
    public String getModelUrl() { return modelUrl; }
    public Boolean getRecommendationEnabled() { return recommendationEnabled; }
    public String getModelDestinationKey() { return modelDestinationKey; }
    public String getModelVersion() { return modelVersion; }
    public String getModelPlaces() { return modelPlaces; }
    public String getModelServices() { return modelServices; }
    public Agent getAgent() { return agent; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setGalleryImageOne(String galleryImageOne) { this.galleryImageOne = galleryImageOne; }
    public void setGalleryImageTwo(String galleryImageTwo) { this.galleryImageTwo = galleryImageTwo; }
    public void setGalleryImageThree(String galleryImageThree) { this.galleryImageThree = galleryImageThree; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(String status) { this.status = status; }
    public void setType(String type) { this.type = type; }
    public void setCity(String city) { this.city = city; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public void setParking(Integer parking) { this.parking = parking; }
    public void setArea(Integer area) { this.area = area; }
    public void setYearBuilt(Integer yearBuilt) { this.yearBuilt = yearBuilt; }
    public void setDescription(String description) { this.description = description; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    public void setModelMaTour(String modelMaTour) { this.modelMaTour = modelMaTour; }
    public void setModelSource(String modelSource) { this.modelSource = modelSource; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public void setRecommendationEnabled(Boolean recommendationEnabled) { this.recommendationEnabled = recommendationEnabled; }
    public void setModelDestinationKey(String modelDestinationKey) { this.modelDestinationKey = modelDestinationKey; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    public void setModelPlaces(String modelPlaces) { this.modelPlaces = modelPlaces; }
    public void setModelServices(String modelServices) { this.modelServices = modelServices; }
    public void setAgent(Agent agent) { this.agent = agent; }
}
