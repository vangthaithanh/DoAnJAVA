package com.example.webdulich.repository;

import com.example.webdulich.entity.TourReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TourReviewRepository extends JpaRepository<TourReview, Long> {

    Optional<TourReview> findByUserIdAndPaymentOrderIdAndPropertyId(Long userId, Long paymentOrderId, Long propertyId);

    List<TourReview> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<TourReview> findByPaymentOrderIdAndUserId(Long paymentOrderId, Long userId);

    long countByUserId(Long userId);
}
