package com.example.webdulich.controller;

import com.example.webdulich.service.AgentService;
import com.example.webdulich.service.BlogService;
import com.example.webdulich.service.PropertyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PropertyService propertyService;
    private final AgentService agentService;
    private final BlogService blogService;

    public HomeController(PropertyService propertyService, AgentService agentService, BlogService blogService) {
        this.propertyService = propertyService;
        this.agentService = agentService;
        this.blogService = blogService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "WebDuLich - Tour Việt Nam & lịch trình thông minh");
        model.addAttribute("activePage", "home");
        model.addAttribute("properties", propertyService.findLatestSix());
        model.addAttribute("tours", propertyService.findLatestSix());
        model.addAttribute("featuredProperty", propertyService.getFeaturedProperty());
        model.addAttribute("featuredTour", propertyService.getFeaturedProperty());
        model.addAttribute("agents", agentService.findAll());
        model.addAttribute("blogs", blogService.findLatestThree());

        model.addAttribute("singleFamilyCount", propertyService.countByType("Tour miền Bắc"));
        model.addAttribute("duplexCount", propertyService.countByType("Tour miền Trung"));
        model.addAttribute("containerHomeCount", propertyService.countByType("Tour biển đảo"));

        model.addAttribute("californiaCount", propertyService.countByCity("Đà Nẵng"));
        model.addAttribute("moroccoCount", propertyService.countByCity("Hà Nội"));
        model.addAttribute("namibiaCount", propertyService.countByCity("Phú Quốc"));

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "Giới thiệu - WebDuLich");
        model.addAttribute("activePage", "about");
        model.addAttribute("agents", agentService.findAll());
        model.addAttribute("propertiesCount", propertyService.findAll().size());
        return "about";
    }
}
