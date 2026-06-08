package com.example.webdulich.repository;

import com.example.webdulich.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByOrderId(String orderId);

    Optional<PaymentOrder> findByRequestId(String requestId);

    List<PaymentOrder> findByUserIdAndStatusIgnoreCaseOrderByCreatedAtDesc(Long userId, String status);

    Optional<PaymentOrder> findByOrderIdAndUserIdAndStatusIgnoreCase(String orderId, Long userId, String status);

    long countByUserIdAndStatusIgnoreCase(Long userId, String status);
}