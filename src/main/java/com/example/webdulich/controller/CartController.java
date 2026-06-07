package com.example.webdulich.controller;

import com.example.webdulich.entity.Property;
import com.example.webdulich.service.PropertyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private static final String CART_SESSION_KEY = "cartTourIds";

    private final PropertyService propertyService;

    public CartController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public String index(Model model, HttpSession session) {
        List<Property> cartTours = getCartTours(session);

        BigDecimal total = cartTours.stream()
                .map(Property::getPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("pageTitle", "Giỏ tour - WebDuLich");
        model.addAttribute("activePage", "cart");
        model.addAttribute("cartTours", cartTours);
        model.addAttribute("cartTotal", total);
        model.addAttribute("cartCount", cartTours.size());

        return "cart/index";
    }

    @PostMapping("/add/{id}")
    public String add(@PathVariable Long id,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {

        if (propertyService.findById(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("successMessage", "Không tìm thấy tour cần thêm vào giỏ.");
            return "redirect:/tours";
        }

        List<Long> tourIds = getCartTourIds(session);
        tourIds.add(id);

        List<Long> uniqueIds = new ArrayList<>(new LinkedHashSet<>(tourIds));
        session.setAttribute(CART_SESSION_KEY, uniqueIds);

        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm tour vào giỏ.");
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        List<Long> tourIds = getCartTourIds(session);
        tourIds.removeIf(value -> value.equals(id));
        session.setAttribute(CART_SESSION_KEY, tourIds);

        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tour khỏi giỏ.");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(CART_SESSION_KEY);
        redirectAttributes.addFlashAttribute("successMessage", "Đã làm trống giỏ tour.");
        return "redirect:/cart";
    }

    @SuppressWarnings("unchecked")
    private List<Long> getCartTourIds(HttpSession session) {
        Object value = session.getAttribute(CART_SESSION_KEY);

        if (value instanceof List<?>) {
            return new ArrayList<>((List<Long>) value);
        }

        return new ArrayList<>();
    }

    private List<Property> getCartTours(HttpSession session) {
        return getCartTourIds(session).stream()
                .distinct()
                .map(propertyService::findById)
                .flatMap(java.util.Optional::stream)
                .toList();
    }
}