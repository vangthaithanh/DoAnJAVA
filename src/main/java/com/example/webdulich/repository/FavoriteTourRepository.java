package com.example.webdulich.repository;

import com.example.webdulich.entity.FavoriteTour;
import com.example.webdulich.entity.FavoriteTourId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteTourRepository extends JpaRepository<FavoriteTour, FavoriteTourId> {

    boolean existsByUserIdAndTourId(Long userId, Long tourId);

    long countByUserId(Long userId);

    List<FavoriteTour> findByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteByUserIdAndTourId(Long userId, Long tourId);
}
