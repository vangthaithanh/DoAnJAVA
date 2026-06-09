package com.example.webdulich.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
public class Inquiry {

    public static final String SOURCE_TOUR = "TOUR";
    public static final String SOURCE_CONTACT = "CONTACT";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_HANDLED = "HANDLED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Column(nullable = false, length = 120)
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Column(nullable = false, length = 120)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 30)
    private String source = SOURCE_TOUR;

    @Column(length = 30)
    private String status = STATUS_PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private Agent assignedAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_agent_id")
    private Agent handledByAgent;

    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    @Column(name = "consultant_note", columnDefinition = "TEXT")
    private String consultantNote;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @PrePersist
    public void beforeCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (source == null || source.isBlank()) {
            source = SOURCE_TOUR;
        }
        if (status == null || status.isBlank()) {
            status = STATUS_PENDING;
        }
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMessage() { return message; }
    public String getSource() { return source; }
    public String getStatus() { return status; }
    public Agent getAssignedAgent() { return assignedAgent; }
    public Agent getHandledByAgent() { return handledByAgent; }
    public LocalDateTime getHandledAt() { return handledAt; }
    public String getConsultantNote() { return consultantNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Property getProperty() { return property; }
    public String getSourceDisplay() {
        return SOURCE_CONTACT.equalsIgnoreCase(source) ? "Liên hệ" : "Tour";
    }
    public String getStatusDisplay() {
        return STATUS_HANDLED.equalsIgnoreCase(status) ? "Đã xử lý" : "Đang chờ";
    }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMessage(String message) { this.message = message; }
    public void setSource(String source) { this.source = source; }
    public void setStatus(String status) { this.status = status; }
    public void setAssignedAgent(Agent assignedAgent) { this.assignedAgent = assignedAgent; }
    public void setHandledByAgent(Agent handledByAgent) { this.handledByAgent = handledByAgent; }
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }
    public void setConsultantNote(String consultantNote) { this.consultantNote = consultantNote; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setProperty(Property property) { this.property = property; }
}
