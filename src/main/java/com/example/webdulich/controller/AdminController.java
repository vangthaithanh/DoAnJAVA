package com.example.webdulich.controller;

import com.example.webdulich.entity.BlogPost;
import com.example.webdulich.entity.CustomItinerary;
import com.example.webdulich.entity.Inquiry;
import com.example.webdulich.entity.Property;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.BlogPostRepository;
import com.example.webdulich.repository.CustomItineraryRepository;
import com.example.webdulich.repository.InquiryRepository;
import com.example.webdulich.repository.PropertyRepository;
import com.example.webdulich.repository.UserRepository;
import com.example.webdulich.service.PropertyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;
    private final InquiryRepository inquiryRepository;
    private final BlogPostRepository blogPostRepository;
    private final CustomItineraryRepository customItineraryRepository;

    public AdminController(
            UserRepository userRepository,
            PropertyRepository propertyRepository,
            PropertyService propertyService,
            InquiryRepository inquiryRepository,
            BlogPostRepository blogPostRepository,
            CustomItineraryRepository customItineraryRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.propertyService = propertyService;
        this.inquiryRepository = inquiryRepository;
        this.blogPostRepository = blogPostRepository;
        this.customItineraryRepository = customItineraryRepository;
    }

    private boolean isAdmin(HttpSession session) {
        return session.getAttribute("currentUserId") != null
                && "ADMIN".equalsIgnoreCase(String.valueOf(session.getAttribute("currentUserRole")));
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("currentUserId") == null) {
            return "redirect:/login";
        }
        if (!isAdmin(session)) {
            return "redirect:/";
        }

        List<Property> latestTours = propertyService.findLatestThree();
        List<User> allUsers = userRepository.findAll();
        List<Property> allProperties = propertyRepository.findAll();
        List<Inquiry> allInquiries = inquiryRepository.findAllByOrderByCreatedAtDesc();
        List<BlogPost> allBlogPosts = blogPostRepository.findAllByOrderByPublishedDateDesc();
        List<CustomItinerary> allItineraries = customItineraryRepository.findAllByOrderByCreatedAtDesc();
        List<CustomItinerary> pendingItineraries = customItineraryRepository.findByStatusOrderByCreatedAtDesc(CustomItinerary.STATUS_PENDING_REVIEW);

        java.util.Map<String, Long> cityTourCount = new java.util.LinkedHashMap<>();
        for (Property p : allProperties) {
            if (p.getCity() != null && !p.getCity().isBlank()) {
                cityTourCount.merge(p.getCity(), 1L, Long::sum);
            }
        }

        model.addAttribute("pageTitle", "Quản trị - WebDuLich");
        model.addAttribute("activePage", "admin");
        model.addAttribute("latestTours", latestTours);
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("latestTourCount", latestTours.size());
        model.addAttribute("propertyCount", propertyRepository.count());
        model.addAttribute("inquiryCount", inquiryRepository.count());
        model.addAttribute("blogPostCount", blogPostRepository.count());
        model.addAttribute("itineraryCount", customItineraryRepository.count());
        model.addAttribute("pendingItineraryCount", customItineraryRepository.countByStatus(CustomItinerary.STATUS_PENDING_REVIEW));
        model.addAttribute("allProperties", allProperties);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allInquiries", allInquiries);
        model.addAttribute("allBlogPosts", allBlogPosts);
        model.addAttribute("allItineraries", allItineraries);
        model.addAttribute("pendingItineraries", pendingItineraries);
        model.addAttribute("cityTourCount", cityTourCount);

        return "admin/dashboard";
    }

    // ==================== TOUR (PROPERTY) CRUD ====================

    @GetMapping("/tours/new")
    public String newTour(Model model, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("pageTitle", "Thêm tour mới - WebDuLich");
        model.addAttribute("formProperty", new Property());
        model.addAttribute("isEdit", false);
        return "admin/tour-form";
    }

    @GetMapping("/tours/edit/{id}")
    public String editTour(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        Property property = propertyRepository.findById(id).orElse(null);
        if (property == null) return "redirect:/admin#tours";
        model.addAttribute("pageTitle", "Sửa tour - WebDuLich");
        model.addAttribute("formProperty", property);
        model.addAttribute("isEdit", true);
        return "admin/tour-form";
    }

    @PostMapping("/tours/save")
    public String saveTour(
            @RequestParam(required = false) Long propertyId,
            @RequestParam String title,
            @RequestParam String price,
            @RequestParam String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String bedrooms,
            @RequestParam(required = false) String bathrooms,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String yearBuilt,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String featured,
            HttpSession session,
            RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";

        Property property;
        if (propertyId != null) {
            property = propertyRepository.findById(propertyId).orElse(new Property());
        } else {
            property = new Property();
            property.setFeatured(false);
            property.setRecommendationEnabled(false);
        }

        property.setTitle(title);
        try { property.setPrice(new BigDecimal(price)); } catch (Exception ignored) {}
        property.setLocation(location);
        property.setType(type != null ? type : "Tour");
        property.setCity(city);
        try { property.setBedrooms(bedrooms != null && !bedrooms.isBlank() ? Integer.parseInt(bedrooms) : null); } catch (Exception ignored) {}
        try { property.setBathrooms(bathrooms != null && !bathrooms.isBlank() ? Integer.parseInt(bathrooms) : null); } catch (Exception ignored) {}
        try { property.setArea(area != null && !area.isBlank() ? Integer.parseInt(area) : null); } catch (Exception ignored) {}
        try { property.setYearBuilt(yearBuilt != null && !yearBuilt.isBlank() ? Integer.parseInt(yearBuilt) : null); } catch (Exception ignored) {}
        property.setDescription(description);
        property.setImageUrl(imageUrl);
        property.setStatus(status != null && !status.isBlank() ? status : "Đang mở bán");
        property.setFeatured("on".equalsIgnoreCase(featured));

        propertyRepository.save(property);
        ra.addFlashAttribute("successMessage", propertyId != null ? "Đã cập nhật tour thành công!" : "Đã thêm tour mới thành công!");
        return "redirect:/admin#tours";
    }

    @PostMapping("/users/toggle-status")
    public String toggleUserStatus(@RequestParam Long userId, @RequestParam String status, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        userRepository.findById(userId).ifPresent(user -> {
            user.setStatus("ACTIVE".equalsIgnoreCase(status) ? "INACTIVE" : "ACTIVE");
            userRepository.save(user);
        });
        return "redirect:/admin#users";
    }

    @PostMapping("/properties/toggle-status")
    public String togglePropertyStatus(@RequestParam Long propertyId, @RequestParam String status, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        propertyRepository.findById(propertyId).ifPresent(property -> {
            property.setStatus("Đang mở bán".equalsIgnoreCase(status) ? "Đã đóng" : "Đang mở bán");
            propertyRepository.save(property);
        });
        return "redirect:/admin#tours";
    }

    @PostMapping("/properties/delete")
    public String deleteProperty(@RequestParam Long propertyId, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        propertyRepository.deleteById(propertyId);
        return "redirect:/admin#tours";
    }

    // ==================== INQUIRY ====================

    @PostMapping("/inquiries/delete")
    public String deleteInquiry(@RequestParam Long inquiryId, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        inquiryRepository.deleteById(inquiryId);
        return "redirect:/admin#bookings";
    }

    // ==================== BLOG CRUD ====================

    @GetMapping("/blog/new")
    public String newBlog(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        model.addAttribute("pageTitle", "Thêm bài viết - WebDuLich");
        model.addAttribute("formBlog", new BlogPost());
        model.addAttribute("isEdit", false);
        return "admin/blog-form";
    }

    @GetMapping("/blog/edit/{id}")
    public String editBlog(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        BlogPost blog = blogPostRepository.findById(id).orElse(null);
        if (blog == null) return "redirect:/admin#content";
        model.addAttribute("pageTitle", "Sửa bài viết - WebDuLich");
        model.addAttribute("formBlog", blog);
        model.addAttribute("isEdit", true);
        return "admin/blog-form";
    }

    @PostMapping("/blog/save")
    public String saveBlog(
            @RequestParam(required = false) Long blogId,
            @RequestParam String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String summary,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String publishedDate,
            HttpSession session,
            RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";

        BlogPost blog;
        if (blogId != null) {
            blog = blogPostRepository.findById(blogId).orElse(new BlogPost());
        } else {
            blog = new BlogPost();
            blog.setPublishedDate(LocalDate.now());
        }

        blog.setTitle(title);
        blog.setCategory(category);
        blog.setAuthor(author);
        blog.setSummary(summary);
        blog.setContent(content);
        blog.setImageUrl(imageUrl);
        if (publishedDate != null && !publishedDate.isBlank()) {
            try { blog.setPublishedDate(LocalDate.parse(publishedDate)); } catch (Exception ignored) {}
        }

        blogPostRepository.save(blog);
        ra.addFlashAttribute("successMessage", blogId != null ? "Đã cập nhật bài viết thành công!" : "Đã thêm bài viết mới thành công!");
        return "redirect:/admin#content";
    }

    @PostMapping("/blog/delete")
    public String deleteBlogPost(@RequestParam Long blogId, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        blogPostRepository.deleteById(blogId);
        return "redirect:/admin#content";
    }

    // ==================== ITINERARY ====================

    @PostMapping("/itineraries/advise")
    public String adviseItinerary(@RequestParam Long itineraryId, @RequestParam String adminNote, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        customItineraryRepository.findById(itineraryId).ifPresent(itinerary -> {
            itinerary.setStatus(CustomItinerary.STATUS_ADVISED);
            itinerary.setAdminNote(adminNote);
            customItineraryRepository.save(itinerary);
        });
        return "redirect:/admin#bookings";
    }

    @PostMapping("/itineraries/delete")
    public String deleteItinerary(@RequestParam Long itineraryId, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        customItineraryRepository.deleteById(itineraryId);
        return "redirect:/admin#bookings";
    }
}
