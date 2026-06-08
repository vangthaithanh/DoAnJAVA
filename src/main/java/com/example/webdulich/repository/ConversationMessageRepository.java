package com.example.webdulich.repository;

import com.example.webdulich.entity.ConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {

    List<ConversationMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    long countByConversationIdAndSenderIdNotAndIsReadFalse(Long conversationId, Long viewerId);

    @Modifying
    @Query("UPDATE ConversationMessage m SET m.isRead = true WHERE m.conversation.id = :convId AND m.sender.id != :viewerId AND m.isRead = false")
    int markAsRead(@Param("convId") Long conversationId, @Param("viewerId") Long viewerId);
}
