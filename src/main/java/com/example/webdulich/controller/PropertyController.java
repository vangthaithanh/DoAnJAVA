package com.example.webdulich.controller;

import com.example.webdulich.entity.Inquiry;
import com.example.webdulich.entity.Property;
import com.example.webdulich.service.InquiryService;
import com.example.webdulich.service.PropertyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/tours", "/properties"})
public class PropertyController {

    private final PropertyService propertyService;
    private final InquiryService inquiryService;
    private final ObjectMapper objectMapper;

    public PropertyController(PropertyService propertyService, InquiryService inquiryService, ObjectMapper objectMapper) {
        this.propertyService = propertyService;
        this.inquiryService = inquiryService;
        this.objectMapper = objectMapper;
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
    public String detail(@PathVariable Long id, Model model) {
        Property property = propertyService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tour id = " + id));

        model.addAttribute("pageTitle", property.getTitle() + " - WebDuLich");
        model.addAttribute("activePage", "tours");
        model.addAttribute("property", property);
        model.addAttribute("inquiry", new Inquiry());
        model.addAttribute("relatedProperties", propertyService.findLatestThree());
        model.addAttribute("relatedTours", propertyService.findLatestThree());
        addModelTourHighlights(model, property);
        return "properties/detail";
    }

    @PostMapping("/{id}/inquiry")
    public String sendInquiry(@PathVariable Long id,
                              @Valid @ModelAttribute("inquiry") Inquiry inquiry,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        Property property = propertyService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tour id = " + id));

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", property.getTitle() + " - WebDuLich");
            model.addAttribute("activePage", "tours");
            model.addAttribute("property", property);
            model.addAttribute("relatedProperties", propertyService.findLatestThree());
            model.addAttribute("relatedTours", propertyService.findLatestThree());
            addModelTourHighlights(model, property);
            return "properties/detail";
        }

        inquiry.setProperty(property);
        inquiryService.save(inquiry);
        redirectAttributes.addFlashAttribute("successMessage", "Đã gửi yêu cầu tư vấn/đặt tour thành công!");
        return "redirect:/tours/" + id;
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
