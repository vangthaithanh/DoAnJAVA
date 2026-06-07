package com.example.webdulich.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên nhân viên không được để trống")
    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(length = 100)
    private String role;

    @Email(message = "Email không hợp lệ")
    @Column(length = 120)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 120)
    private String facebookUrl;

    @Column(length = 120)
    private String twitterUrl;

    @Column(length = 120)
    private String linkedinUrl;

    public Agent() {
    }

    public Agent(String fullName, String role, String email, String phone, String avatarUrl, String bio,
                 String facebookUrl, String twitterUrl, String linkedinUrl) {
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.facebookUrl = facebookUrl;
        this.twitterUrl = twitterUrl;
        this.linkedinUrl = linkedinUrl;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getBio() { return bio; }
    public String getFacebookUrl() { return facebookUrl; }
    public String getTwitterUrl() { return twitterUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }

    public void setId(Long id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setBio(String bio) { this.bio = bio; }
    public void setFacebookUrl(String facebookUrl) { this.facebookUrl = facebookUrl; }
    public void setTwitterUrl(String twitterUrl) { this.twitterUrl = twitterUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
}
