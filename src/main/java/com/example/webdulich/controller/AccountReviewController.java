package com.example.webdulich.controller;

import com.example.webdulich.entity.TourReview;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.TourReviewRepository;
import com.example.webdulich.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/account/reviews")
public class AccountReviewController {

    private final TourReviewRepository tourReviewRepository;
    private final UserRepository userRepository;

    public AccountReviewController(TourReviewRepository tourReviewRepository,
                                   UserRepository userRepository) {
        this.tourReviewRepository = tourReviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String reviews(Model model, HttpSession session) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<TourReview> reviews = tourReviewRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());

        model.addAttribute("pageTitle", "Đánh giá của tôi - WebDuLich");
        model.addAttribute("activePage", "account");
        model.addAttribute("accountUser", currentUser);
        model.addAttribute("reviews", reviews);

        return "account/reviews";
    }

    @PostMapping("/{reviewId}/update")
    public String updateReview(@PathVariable Long reviewId,
                               @RequestParam Integer rating,
                               @RequestParam(required = false) String content,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            if (rating == null || rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Số sao phải từ 1 đến 5.");
            }

            TourReview review = tourReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá."));

            if (!review.getUserId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Bạn không có quyền sửa đánh giá này.");
            }

            review.setRating(rating);
            review.setContent(content == null ? "" : content.trim());

            tourReviewRepository.save(review);

            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật đánh giá.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/account/reviews";
    }

    @PostMapping("/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            TourReview review = tourReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá."));

            if (!review.getUserId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Bạn không có quyền xóa đánh giá này.");
            }

            tourReviewRepository.delete(review);

            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa đánh giá.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/account/reviews";
    }

    private User getCurrentUser(HttpSession session) {
        Long currentUserId = getCurrentUserId(session);

        if (currentUserId == null) {
            return null;
        }

        return userRepository.findById(currentUserId).orElse(null);
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
