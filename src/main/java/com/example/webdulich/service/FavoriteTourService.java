package com.example.webdulich.service;

import com.example.webdulich.entity.FavoriteTour;
import com.example.webdulich.entity.Property;
import com.example.webdulich.repository.FavoriteTourRepository;
import com.example.webdulich.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteTourService {

    private final FavoriteTourRepository favoriteTourRepository;
    private final PropertyRepository propertyRepository;

    public FavoriteTourService(FavoriteTourRepository favoriteTourRepository,
                               PropertyRepository propertyRepository) {
        this.favoriteTourRepository = favoriteTourRepository;
        this.propertyRepository = propertyRepository;
    }

    public void add(Long userId, Long tourId) {
        validateUser(userId);
        propertyRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tour cần lưu yêu thích."));

        if (!favoriteTourRepository.existsByUserIdAndTourId(userId, tourId)) {
            favoriteTourRepository.save(new FavoriteTour(userId, tourId));
        }
    }

    @Transactional
    public void remove(Long userId, Long tourId) {
        validateUser(userId);
        favoriteTourRepository.deleteByUserIdAndTourId(userId, tourId);
    }

    public boolean isFavorite(Long userId, Long tourId) {
        return userId != null && favoriteTourRepository.existsByUserIdAndTourId(userId, tourId);
    }

    public long countByUser(Long userId) {
        validateUser(userId);
        return favoriteTourRepository.countByUserId(userId);
    }

    public List<Property> findToursByUser(Long userId) {
        validateUser(userId);
        return favoriteTourRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(FavoriteTour::getTour)
                .filter(tour -> tour != null)
                .toList();
    }

    private void validateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để quản lý tour yêu thích.");
        }
    }
}
