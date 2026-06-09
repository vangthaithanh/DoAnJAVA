package com.example.webdulich.controller;

import com.example.webdulich.entity.ContactMessage;
import com.example.webdulich.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    private final InquiryService inquiryService;

    public ContactController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        model.addAttribute("pageTitle", "Liên hệ - WebDuLich");
        model.addAttribute("activePage", "contact");

        if (!model.containsAttribute("contactMessage")) {
            model.addAttribute("contactMessage", new ContactMessage());
        }

        return "contact/index";
    }

    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactMessage") ContactMessage contactMessage,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Liên hệ - WebDuLich");
            model.addAttribute("activePage", "contact");
            return "contact/index";
        }

        inquiryService.createContactInquiry(contactMessage);
        redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã liên hệ. WebDuLich sẽ phản hồi trong thời gian sớm nhất!");

        return "redirect:/contact";
    }

    @PostMapping("/newsletter")
    public String newsletter(@RequestParam String email,
                             RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("successMessage", "Đăng ký nhận thông tin ưu đãi thành công: " + email);
        return "redirect:/";
    }
}
