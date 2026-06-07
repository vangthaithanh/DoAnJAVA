package com.example.webdulich.controller;

import com.example.webdulich.service.CustomItineraryService;
import com.example.webdulich.service.PropertyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class TravelController {

    private final PropertyService propertyService;
    private final CustomItineraryService customItineraryService;

    public TravelController(PropertyService propertyService,
                            CustomItineraryService customItineraryService) {
        this.propertyService = propertyService;
        this.customItineraryService = customItineraryService;
    }

    @GetMapping("/itinerary")
    public String itinerary(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("currentUserId") == null) {
            session.setAttribute("afterLoginRedirect", "/itinerary");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập hoặc tạo tài khoản để tự tạo lịch trình.");
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Tạo lịch trình du lịch - WebDuLich");
        model.addAttribute("activePage", "itinerary");
        model.addAttribute("tours", propertyService.findLatestThree());
        return "travel/itinerary";
    }

    @PostMapping("/itinerary")
    public String createItinerary(@RequestParam String destinationText,
                                  @RequestParam Integer totalDays,
                                  @RequestParam(required = false) BigDecimal budget,
                                  @RequestParam(required = false) String travelStyle,
                                  @RequestParam(required = false) String note,
                                  @RequestParam(required = false) String modelDestinationKey,
                                  @RequestParam(required = false) String selectedPlaces,
                                  @RequestParam(required = false) String selectedServices,
                                  @RequestParam(required = false) Long selectedPropertyId,
                                  @RequestParam(required = false) String selectedModelMaTour,
                                  @RequestParam(required = false) String selectedTourTitle,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Long currentUserId = getCurrentUserId(session);

        if (currentUserId == null) {
            session.setAttribute("afterLoginRedirect", "/itinerary");
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập hoặc tạo tài khoản để lưu lịch trình.");
            return "redirect:/login";
        }

        try {
            var itinerary = customItineraryService.create(
                    currentUserId,
                    destinationText,
                    totalDays,
                    budget,
                    travelStyle,
                    note,
                    modelDestinationKey,
                    selectedPlaces,
                    selectedServices,
                    selectedPropertyId,
                    selectedModelMaTour,
                    selectedTourTitle);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đã lưu lịch trình \"" + itinerary.getTitle() + "\" vào tài khoản của bạn.");
            return "redirect:/account";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            redirectAttributes.addFlashAttribute("destinationText", destinationText);
            redirectAttributes.addFlashAttribute("totalDays", totalDays);
            redirectAttributes.addFlashAttribute("budget", budget);
            redirectAttributes.addFlashAttribute("travelStyle", travelStyle);
            redirectAttributes.addFlashAttribute("note", note);
            redirectAttributes.addFlashAttribute("modelDestinationKey", modelDestinationKey);
            redirectAttributes.addFlashAttribute("selectedPlaces", selectedPlaces);
            redirectAttributes.addFlashAttribute("selectedServices", selectedServices);
            redirectAttributes.addFlashAttribute("selectedPropertyId", selectedPropertyId);
            redirectAttributes.addFlashAttribute("selectedModelMaTour", selectedModelMaTour);
            redirectAttributes.addFlashAttribute("selectedTourTitle", selectedTourTitle);
            return "redirect:/itinerary";
        }
    }

    @GetMapping({"/custom-tour", "/suggestions"})
    public String mergedPagesRedirect() {
        return "redirect:/tours";
    }

    @GetMapping("/destinations")
    public String destinations(Model model) {
        model.addAttribute("pageTitle", "Điểm đến Việt Nam - WebDuLich");
        model.addAttribute("activePage", "destinations");
        return "travel/destinations";
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
