package com.example.webdulich.controller;

import com.example.webdulich.entity.User;
import com.example.webdulich.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        if (session.getAttribute("currentUserId") != null) {
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Đăng nhập - WebDuLich");
        model.addAttribute("activePage", "login");
        return "auth/login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        var userOptional = authService.login(email, password);

        if (userOptional.isEmpty()) {
            model.addAttribute("pageTitle", "Đăng nhập - WebDuLich");
            model.addAttribute("activePage", "login");
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không chính xác.");
            model.addAttribute("email", email);
            return "auth/login";
        }

        User user = userOptional.get();

        session.setAttribute("currentUserId", user.getId());
        session.setAttribute("currentUserName", user.getFullName());
        session.setAttribute("currentUserEmail", user.getEmail());
        session.setAttribute("currentUserRole", user.getRole());

        redirectAttributes.addFlashAttribute("successMessage", "Đăng nhập thành công. Chào mừng " + user.getFullName() + "!");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        if (session.getAttribute("currentUserId") != null) {
            return "redirect:/";
        }

        model.addAttribute("pageTitle", "Đăng ký - WebDuLich");
        model.addAttribute("activePage", "register");
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        try {
            authService.register(fullName, email, phone, password, confirmPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo tài khoản thành công. Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("pageTitle", "Đăng ký - WebDuLich");
            model.addAttribute("activePage", "register");
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("fullName", fullName);
            model.addAttribute("email", email);
            model.addAttribute("phone", phone);
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã đăng xuất khỏi hệ thống.");
        return "redirect:/";
    }
}