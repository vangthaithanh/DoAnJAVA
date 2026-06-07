package com.example.webdulich.controller;

import com.example.webdulich.entity.User;
import com.example.webdulich.repository.UserRepository;
import com.example.webdulich.service.CustomItineraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    private final UserRepository userRepository;
    private final CustomItineraryService customItineraryService;

    public AccountController(UserRepository userRepository,
                             CustomItineraryService customItineraryService) {
        this.userRepository = userRepository;
        this.customItineraryService = customItineraryService;
    }

    @GetMapping("/account")
    public String index(Model model, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);

        if (currentUserId == null) {
            return "redirect:/login";
        }

        User currentUser = userRepository.findById(currentUserId).orElse(null);

        if (currentUser == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Trang cá nhân - WebDuLich");
        model.addAttribute("activePage", "account");
        model.addAttribute("accountUser", currentUser);
        model.addAttribute("itineraries", customItineraryService.findByUser(currentUserId));
        model.addAttribute("itineraryCount", customItineraryService.countByUser(currentUserId));

        return "account/index";
    }

    private Long getCurrentUserId(HttpSession session) {
        Object value = session.getAttribute("currentUserId");

        if (value instanceof Long id) {
            return id;
        }

        if (value instanceof Integer id) {
            return id.longValue();
        }

        if (value instanceof String id) {
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException exception) {
                return null;
            }
        }

        return null;
    }
}
