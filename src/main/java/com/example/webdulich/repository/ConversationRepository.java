package com.example.webdulich.repository;

import com.example.webdulich.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByAgentUserIdOrderByUpdatedAtDesc(Long agentUserId);

    List<Conversation> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<Conversation> findByUserIdAndAgentUserIdAndStatus(Long userId, Long agentUserId, String status);

    Optional<Conversation> findByUserIdAndAgentUserIdAndItineraryIdAndStatus(Long userId, Long agentUserId, Long itineraryId, String status);

    Optional<Conversation> findByItineraryIdAndStatus(Long itineraryId, String status);

    Optional<Conversation> findByIdAndStatus(Long id, String status);

    long countByAgentUserIdAndStatus(Long agentUserId, String status);

    @Query("SELECT c FROM Conversation c WHERE c.agentUser.id = :agentId AND c.status = :status ORDER BY c.updatedAt DESC")
    List<Conversation> findActiveByAgentId(@Param("agentId") Long agentId, @Param("status") String status);

    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId AND c.status = :status ORDER BY c.updatedAt DESC")
    List<Conversation> findActiveByUserId(@Param("userId") Long userId, @Param("status") String status);

    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId OR c.agentUser.id = :userId ORDER BY c.updatedAt DESC")
    List<Conversation> findAllByParticipant(@Param("userId") Long userId);
}
