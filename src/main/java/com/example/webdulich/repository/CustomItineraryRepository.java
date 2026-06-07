package com.example.webdulich.repository;

import com.example.webdulich.entity.CustomItinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomItineraryRepository extends JpaRepository<CustomItinerary, Long> {

    List<CustomItinerary> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);
}
