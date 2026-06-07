package com.example.webdulich.controller;

import com.example.webdulich.service.PropertyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TravelController {

    private final PropertyService propertyService;

    public TravelController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/itinerary")
    public String itinerary(Model model) {
        model.addAttribute("pageTitle", "Tạo lịch trình du lịch - WebDuLich");
        model.addAttribute("activePage", "itinerary");
        model.addAttribute("tours", propertyService.findLatestThree());
        return "travel/itinerary";
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
}
