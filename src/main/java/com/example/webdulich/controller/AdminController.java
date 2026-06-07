package com.example.webdulich.controller;

import com.example.webdulich.repository.UserRepository;
import com.example.webdulich.service.PropertyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final PropertyService propertyService;

    public AdminController(UserRepository userRepository, PropertyService propertyService) {
        this.userRepository = userRepository;
        this.propertyService = propertyService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("currentUserId") == null) {
            return "redirect:/login";
        }

        Object role = session.getAttribute("currentUserRole");

        if (role == null || !"ADMIN".equalsIgnoreCase(role.toString())) {
            return "redirect:/";
        }

        var latestTours = propertyService.findLatestThree();

        model.addAttribute("pageTitle", "Quản trị - WebDuLich");
        model.addAttribute("activePage", "admin");
        model.addAttribute("latestTours", latestTours);
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("latestTourCount", latestTours.size());

        return "admin/dashboard";
    }
}