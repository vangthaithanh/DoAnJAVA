package com.example.webdulich.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CLOSED = "CLOSED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_user_id")
    private User agentUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_id")
    private CustomItinerary itinerary;

    @Column(nullable = false, length = 30)
    private String status = STATUS_ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private List<ConversationMessage> messages = new ArrayList<>();

    @Transient
    private long unreadCount = 0;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        if (status == null || status.isBlank()) {
            status = STATUS_ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public User getAgentUser() { return agentUser; }
    public CustomItinerary getItinerary() { return itinerary; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<ConversationMessage> getMessages() { return messages; }
    public long getUnreadCount() { return unreadCount; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setAgentUser(User agentUser) { this.agentUser = agentUser; }
    public void setItinerary(CustomItinerary itinerary) { this.itinerary = itinerary; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setMessages(List<ConversationMessage> messages) { this.messages = messages; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }

    public String getOtherPartyName(Long currentUserId) {
        if (user != null && user.getId().equals(currentUserId)) {
            return agentUser != null ? agentUser.getFullName() : "Tư vấn viên";
        }
        return user != null ? user.getFullName() : "Khách hàng";
    }

    public String getLastMessagePreview() {
        if (messages == null || messages.isEmpty()) {
            return "Chưa có tin nhắn";
        }
        ConversationMessage last = messages.get(messages.size() - 1);
        String content = last.getContent();
        if (content == null) return "";
        return content.length() > 60 ? content.substring(0, 60) + "..." : content;
    }

    public boolean isActive() { return STATUS_ACTIVE.equals(status); }
    public boolean isClosed() { return STATUS_CLOSED.equals(status); }
}
