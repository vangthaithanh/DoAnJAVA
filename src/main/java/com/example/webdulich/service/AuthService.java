package com.example.webdulich.service;

import com.example.webdulich.entity.User;
import com.example.webdulich.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String fullName, String email, String phone, String password, String confirmPassword) {
        fullName = normalizeText(fullName);
        email = normalizeEmail(email);
        phone = normalizeText(phone);

        if (fullName.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập họ tên.");
        }

        if (email.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập email.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email không hợp lệ.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu.");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự.");
        }

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp.");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email này đã được sử dụng.");
        }

        String passwordHash = passwordEncoder.encode(password);

        User user = new User(fullName, email, phone, passwordHash);
        user.setRole("USER");
        user.setStatus("ACTIVE");

        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        email = normalizeEmail(email);

        if (email.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return Optional.empty();
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}