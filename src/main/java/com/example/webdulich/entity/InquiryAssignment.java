package com.example.webdulich.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry_assignments")
public class InquiryAssignment {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_HANDLED = "HANDLED";
    public static final String STATUS_COLLEAGUE_HANDLED = "COLLEAGUE_HANDLED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @Column(length = 30, nullable = false)
    private String status = STATUS_PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    @PrePersist
    public void beforeCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = STATUS_PENDING;
        }
    }

    public Long getId() { return id; }
    public Inquiry getInquiry() { return inquiry; }
    public Agent getAgent() { return agent; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getHandledAt() { return handledAt; }
    public String getStatusDisplay() {
        return switch (status == null ? STATUS_PENDING : status) {
            case STATUS_HANDLED -> "Bạn đã xử lý";
            case STATUS_COLLEAGUE_HANDLED -> "Đồng nghiệp đã xử lý";
            default -> "Đang chờ";
        };
    }

    public void setId(Long id) { this.id = id; }
    public void setInquiry(Inquiry inquiry) { this.inquiry = inquiry; }
    public void setAgent(Agent agent) { this.agent = agent; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }
}
