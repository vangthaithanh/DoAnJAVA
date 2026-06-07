package com.example.webdulich.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
public class Inquiry {

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

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @PrePersist
    public void beforeCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Property getProperty() { return property; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setProperty(Property property) { this.property = property; }
}
