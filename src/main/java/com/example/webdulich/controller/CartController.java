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
    private static final String PENDING_CART_TOUR_KEY = "pendingCartTourId";

    private final PropertyService propertyService;

    public CartController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public String index(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(session)) {
            session.setAttribute("afterLoginRedirect", "/cart");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập hoặc tạo tài khoản để xem giỏ tour.");
            return "redirect:/login";
        }

        consumePendingCartTour(session);
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

        if (!isLoggedIn(session)) {
            session.setAttribute(PENDING_CART_TOUR_KEY, id);
            session.setAttribute("afterLoginRedirect", "/cart");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập hoặc tạo tài khoản để thêm tour vào giỏ.");
            return "redirect:/login";
        }

        if (propertyService.findById(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tour cần thêm vào giỏ.");
            return "redirect:/tours";
        }

        addTourToSession(session, id);

        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm tour vào giỏ.");
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable Long id,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        if (!isLoggedIn(session)) {
            session.setAttribute("afterLoginRedirect", "/cart");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý giỏ tour.");
            return "redirect:/login";
        }

        List<Long> tourIds = getCartTourIds(session);
        tourIds.removeIf(value -> value.equals(id));
        session.setAttribute(CART_SESSION_KEY, tourIds);

        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tour khỏi giỏ.");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(session)) {
            session.setAttribute("afterLoginRedirect", "/cart");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để quản lý giỏ tour.");
            return "redirect:/login";
        }

        session.removeAttribute(CART_SESSION_KEY);
        redirectAttributes.addFlashAttribute("successMessage", "Đã làm trống giỏ tour.");
        return "redirect:/cart";
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("currentUserId") != null;
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

    private void addTourToSession(HttpSession session, Long id) {
        List<Long> tourIds = getCartTourIds(session);
        tourIds.add(id);
        session.setAttribute(CART_SESSION_KEY, new ArrayList<>(new LinkedHashSet<>(tourIds)));
    }

    private void consumePendingCartTour(HttpSession session) {
        Object value = session.getAttribute(PENDING_CART_TOUR_KEY);
        session.removeAttribute(PENDING_CART_TOUR_KEY);

        Long tourId = null;
        if (value instanceof Long id) {
            tourId = id;
        } else if (value instanceof Integer id) {
            tourId = id.longValue();
        } else if (value instanceof String id) {
            try {
                tourId = Long.parseLong(id);
            } catch (NumberFormatException ignored) {
                tourId = null;
            }
        }

        if (tourId != null && propertyService.findById(tourId).isPresent()) {
            addTourToSession(session, tourId);
        }
    }
}
