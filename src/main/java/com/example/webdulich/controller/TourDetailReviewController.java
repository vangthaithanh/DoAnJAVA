package com.example.webdulich.controller;

import com.example.webdulich.entity.TourReview;
import com.example.webdulich.repository.TourReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tours/{tourId}/reviews")
public class TourDetailReviewController {

    private final TourReviewRepository tourReviewRepository;

    public TourDetailReviewController(TourReviewRepository tourReviewRepository) {
        this.tourReviewRepository = tourReviewRepository;
    }

    @PostMapping("/{reviewId}/update")
    public String updateReviewFromTourDetail(@PathVariable Long tourId,
                                             @PathVariable Long reviewId,
                                             @RequestParam Integer rating,
                                             @RequestParam(required = false) String content,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes) {
        Long currentUserId = getCurrentUserId(session);

        if (currentUserId == null) {
            session.setAttribute("afterLoginRedirect", "/tours/" + tourId + "#tourReviews");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để sửa đánh giá.");
            return "redirect:/login";
        }

        try {
            if (rating == null || rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Số sao đánh giá phải từ 1 đến 5.");
            }

            TourReview review = tourReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá."));

            if (!tourId.equals(review.getPropertyId())) {
                throw new IllegalArgumentException("Đánh giá này không thuộc tour hiện tại.");
            }

            if (!currentUserId.equals(review.getUserId())) {
                throw new IllegalArgumentException("Bạn không có quyền sửa đánh giá này.");
            }

            review.setRating(rating);
            review.setContent(content == null ? "" : content.trim());
            tourReviewRepository.save(review);

            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật đánh giá của bạn.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/tours/" + tourId + "#tourReviews";
    }

    @PostMapping("/{reviewId}/delete")
    public String deleteReviewFromTourDetail(@PathVariable Long tourId,
                                             @PathVariable Long reviewId,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes) {
        Long currentUserId = getCurrentUserId(session);

        if (currentUserId == null) {
            session.setAttribute("afterLoginRedirect", "/tours/" + tourId + "#tourReviews");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xóa đánh giá.");
            return "redirect:/login";
        }

        try {
            TourReview review = tourReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá."));

            if (!tourId.equals(review.getPropertyId())) {
                throw new IllegalArgumentException("Đánh giá này không thuộc tour hiện tại.");
            }

            if (!currentUserId.equals(review.getUserId())) {
                throw new IllegalArgumentException("Bạn không có quyền xóa đánh giá này.");
            }

            tourReviewRepository.delete(review);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa đánh giá của bạn.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/tours/" + tourId + "#tourReviews";
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
