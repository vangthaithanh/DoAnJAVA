package com.example.webdulich.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Đăng nhập - WebDuLich");
        model.addAttribute("activePage", "login");
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Đăng ký - WebDuLich");
        model.addAttribute("activePage", "register");
        return "auth/register";
    }
}
