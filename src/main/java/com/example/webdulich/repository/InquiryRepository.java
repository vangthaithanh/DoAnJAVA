package com.example.webdulich.repository;

import com.example.webdulich.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    java.util.List<Inquiry> findAllByOrderByCreatedAtDesc();

    java.util.List<Inquiry> findByAssignedAgentIdOrderByCreatedAtDesc(Long agentId);

    java.util.List<Inquiry> findByEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    long countByAssignedAgentId(Long agentId);

    long countByAssignedAgentIdAndStatus(Long agentId, String status);

    long countByEmailIgnoreCase(String email);
}
