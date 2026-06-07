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
        var agents = agentService.findAll();
        model.addAttribute("pageTitle", "WebDuLich - Tour Việt Nam & lịch trình thông minh");
        model.addAttribute("activePage", "home");
        model.addAttribute("properties", propertyService.findLatestSix());
        model.addAttribute("tours", propertyService.findLatestSix());
        model.addAttribute("featuredProperty", propertyService.getFeaturedProperty());
        model.addAttribute("featuredTour", propertyService.getFeaturedProperty());
        model.addAttribute("agents", agents);
        model.addAttribute("blogs", blogService.findLatestThree());

        model.addAttribute("tourCount", propertyService.countAll());
        model.addAttribute("destinationCount", propertyService.countDistinctCities());
        model.addAttribute("agentCount", agents.size());

        model.addAttribute("northTourCount", propertyService.countByType("Tour miền Bắc"));
        model.addAttribute("centralTourCount", propertyService.countByType("Tour miền Trung"));
        model.addAttribute("southTourCount", propertyService.countByType("Tour miền Nam"));
        model.addAttribute("highlandTourCount", propertyService.countByType("Tour Tây Nguyên"));
        model.addAttribute("islandTourCount", propertyService.countByType("Tour biển đảo"));

        model.addAttribute("daLatCount", propertyService.countByCity("Đà Lạt"));
        model.addAttribute("phanThietCount", propertyService.countByCity("Phan Thiết / Mũi Né"));
        model.addAttribute("vungTauCount", propertyService.countByCity("Vũng Tàu"));

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
