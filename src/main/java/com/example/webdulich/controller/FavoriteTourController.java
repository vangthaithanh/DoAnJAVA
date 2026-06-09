package com.example.webdulich.controller;

import com.example.webdulich.service.FavoriteTourService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FavoriteTourController {

    private final FavoriteTourService favoriteTourService;

    public FavoriteTourController(FavoriteTourService favoriteTourService) {
        this.favoriteTourService = favoriteTourService;
    }

    @PostMapping("/favorites/add/{tourId}")
    public String add(@PathVariable Long tourId,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {

        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            session.setAttribute("afterLoginRedirect", "/tours/" + tourId);
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập hoặc tạo tài khoản để lưu tour yêu thích.");
            return "redirect:/login";
        }

        try {
            favoriteTourService.add(currentUserId, tourId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm tour vào danh sách yêu thích.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/tours/" + tourId;
    }

    @PostMapping("/favorites/remove/{tourId}")
    public String remove(@PathVariable Long tourId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            session.setAttribute("afterLoginRedirect", "/account");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý tour yêu thích.");
            return "redirect:/login";
        }

        favoriteTourService.remove(currentUserId, tourId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tour khỏi danh sách yêu thích.");
        return "redirect:/account";
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
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }
}
