package com.example.webdulich.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorite_tours")
@IdClass(FavoriteTourId.class)
public class FavoriteTour {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "tour_id")
    private Long tourId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", insertable = false, updatable = false)
    private Property tour;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public FavoriteTour() {
    }

    public FavoriteTour(Long userId, Long tourId) {
        this.userId = userId;
        this.tourId = tourId;
    }

    @PrePersist
    public void beforeCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getUserId() { return userId; }
    public Long getTourId() { return tourId; }
    public User getUser() { return user; }
    public Property getTour() { return tour; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public void setUser(User user) { this.user = user; }
    public void setTour(Property tour) { this.tour = tour; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
