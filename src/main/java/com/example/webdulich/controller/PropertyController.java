package com.example.webdulich.controller;

import com.example.webdulich.entity.Inquiry;
import com.example.webdulich.entity.Property;
import com.example.webdulich.entity.TourReview;
import com.example.webdulich.repository.TourReviewRepository;
import com.example.webdulich.service.FavoriteTourService;
import com.example.webdulich.service.InquiryService;
import com.example.webdulich.service.PropertyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping({"/tours", "/properties"})
public class PropertyController {

    private final PropertyService propertyService;
    private final InquiryService inquiryService;
    private final ObjectMapper objectMapper;
    private final TourReviewRepository tourReviewRepository;
    private final FavoriteTourService favoriteTourService;

    public PropertyController(PropertyService propertyService,
                              InquiryService inquiryService,
                              ObjectMapper objectMapper,
                              TourReviewRepository tourReviewRepository,
                              FavoriteTourService favoriteTourService) {
        this.propertyService = propertyService;
        this.inquiryService = inquiryService;
        this.objectMapper = objectMapper;
        this.tourReviewRepository = tourReviewRepository;
        this.favoriteTourService = favoriteTourService;
    }

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) String location,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice) {

        var tours = propertyService.search(keyword, type, location, status, minPrice, maxPrice);
        model.addAttribute("pageTitle", "Tour trong nước - WebDuLich");
        model.addAttribute("activePage", "tours");
        model.addAttribute("properties", tours);
        model.addAttribute("tours", tours);
        model.addAttribute("latestProperties", propertyService.findLatestThree());
        model.addAttribute("latestTours", propertyService.findLatestThree());
        return "properties/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        Property property = propertyService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tour id = " + id));

        addTourReviewData(model, property, session);

        model.addAttribute("pageTitle", property.getTitle() + " - WebDuLich");
        model.addAttribute("activePage", "tours");
        model.addAttribute("property", property);
        model.addAttribute("inquiry", new Inquiry());
        model.addAttribute("relatedProperties", propertyService.findLatestThree());
        model.addAttribute("relatedTours", propertyService.findLatestThree());
        model.addAttribute("isFavoriteTour", favoriteTourService.isFavorite(getCurrentUserId(session), property.getId()));
        addModelTourHighlights(model, property);
        return "properties/detail";
    }

    @PostMapping("/{id}/inquiry")
    public String sendInquiry(@PathVariable Long id,
                              @Valid @ModelAttribute("inquiry") Inquiry inquiry,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpSession session,
                              Model model) {

        Property property = propertyService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tour id = " + id));

        if (session.getAttribute("currentUserId") == null) {
            session.setAttribute("afterLoginRedirect", "/tours/" + id + "#bookingForm");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập hoặc tạo tài khoản để gửi yêu cầu tư vấn tour.");
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            addTourReviewData(model, property, session);
            model.addAttribute("pageTitle", property.getTitle() + " - WebDuLich");
            model.addAttribute("activePage", "tours");
            model.addAttribute("property", property);
            model.addAttribute("relatedProperties", propertyService.findLatestThree());
            model.addAttribute("relatedTours", propertyService.findLatestThree());
            model.addAttribute("isFavoriteTour", favoriteTourService.isFavorite(getCurrentUserId(session), property.getId()));
            addModelTourHighlights(model, property);
            return "properties/detail";
        }

        inquiry.setProperty(property);
        inquiryService.save(inquiry);
        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi yêu cầu tư vấn/đặt tour thành công!");
        return "redirect:/tours/" + id;
    }

    private void addTourReviewData(Model model, Property property, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);

        List<TourReview> tourReviews = tourReviewRepository.findByPropertyIdOrderByUpdatedAtDesc(property.getId());

        Optional<TourReview> currentUserReview = currentUserId == null
                ? Optional.empty()
                : tourReviewRepository.findFirstByPropertyIdAndUserIdOrderByUpdatedAtDesc(property.getId(), currentUserId);

        double averageRating = 0.0;

        if (!tourReviews.isEmpty()) {
            averageRating = tourReviews.stream()
                    .map(TourReview::getRating)
                    .filter(rating -> rating != null)
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
        }

        model.addAttribute("tourReviews", tourReviews);
        model.addAttribute("tourReviewCount", tourReviews.size());
        model.addAttribute("tourAverageRating", averageRating);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("currentUserReview", currentUserReview.orElse(null));
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

    private void addModelTourHighlights(Model model, Property property) {
        model.addAttribute("modelTourPlaces", readJsonList(property.getModelPlaces(), new TypeReference<List<String>>() { }));
        model.addAttribute("modelTourServices", readJsonList(
                property.getModelServices(), new TypeReference<List<Map<String, String>>>() { }));
    }

    private <T> List<T> readJsonList(String value, TypeReference<List<T>> type) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException exception) {
            return List.of();
        }
    } 
}
