package com.example.webdulich.entity;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteTourId implements Serializable {

    private Long userId;
    private Long tourId;

    public FavoriteTourId() {
    }

    public FavoriteTourId(Long userId, Long tourId) {
        this.userId = userId;
        this.tourId = tourId;
    }

    public Long getUserId() { return userId; }
    public Long getTourId() { return tourId; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FavoriteTourId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && Objects.equals(tourId, that.tourId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tourId);
    }
}
