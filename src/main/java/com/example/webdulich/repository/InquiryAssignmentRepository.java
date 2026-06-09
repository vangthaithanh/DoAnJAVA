package com.example.webdulich.repository;

import com.example.webdulich.entity.InquiryAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryAssignmentRepository extends JpaRepository<InquiryAssignment, Long> {

    List<InquiryAssignment> findByAgentIdOrderByCreatedAtDesc(Long agentId);

    List<InquiryAssignment> findByAgentIdAndStatusOrderByCreatedAtDesc(Long agentId, String status);

    List<InquiryAssignment> findByInquiryId(Long inquiryId);

    Optional<InquiryAssignment> findByInquiryIdAndAgentId(Long inquiryId, Long agentId);

    long countByAgentId(Long agentId);

    long countByAgentIdAndStatus(Long agentId, String status);
}
