package com.example.webdulich.controller;

import com.example.webdulich.entity.BlogPost;
import com.example.webdulich.service.BlogService;
import com.example.webdulich.service.PropertyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final BlogService blogService;
    private final PropertyService propertyService;

    public BlogController(BlogService blogService, PropertyService propertyService) {
        this.blogService = blogService;
        this.propertyService = propertyService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Cẩm nang du lịch - WebDuLich");
        model.addAttribute("activePage", "blog");
        model.addAttribute("blogs", blogService.findAll());
        return "blog/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BlogPost blog = blogService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết id = " + id));

        model.addAttribute("pageTitle", blog.getTitle() + " - WebDuLich");
        model.addAttribute("activePage", "blog");
        model.addAttribute("blog", blog);
        model.addAttribute("latestProperties", propertyService.findLatestThree());
        model.addAttribute("latestTours", propertyService.findLatestThree());
        return "blog/detail";
    }
}
