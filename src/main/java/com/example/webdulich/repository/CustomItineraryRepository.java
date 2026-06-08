package com.example.webdulich.repository;

import com.example.webdulich.entity.CustomItinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomItineraryRepository extends JpaRepository<CustomItinerary, Long> {

    List<CustomItinerary> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);

    Optional<CustomItinerary> findByIdAndUserId(Long id, Long userId);

    List<CustomItinerary> findAllByOrderByCreatedAtDesc();

    List<CustomItinerary> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);

    List<CustomItinerary> findByAssignedAgentIdOrderByCreatedAtDesc(Long agentId);

    long countByAssignedAgentId(Long agentId);

    long countByAssignedAgentIdAndStatus(Long agentId, String status);

    Optional<CustomItinerary> findByIdAndAssignedAgentId(Long id, Long agentId);

    List<CustomItinerary> findByStatusInOrderByCreatedAtDesc(List<String> statuses);
}
