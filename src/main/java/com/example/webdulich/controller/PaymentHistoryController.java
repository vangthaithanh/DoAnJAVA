package com.example.webdulich.controller;

import com.example.webdulich.dto.PaymentInvoiceView;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.UserRepository;
import com.example.webdulich.service.PaymentHistoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/account/payments")
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;
    private final UserRepository userRepository;

    public PaymentHistoryController(PaymentHistoryService paymentHistoryService,
                                    UserRepository userRepository) {
        this.paymentHistoryService = paymentHistoryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String payments(Model model, HttpSession session) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<PaymentInvoiceView> invoices = paymentHistoryService.findPaidInvoicesByUser(currentUser.getId());

        model.addAttribute("pageTitle", "Hóa đơn thanh toán - WebDuLich");
        model.addAttribute("activePage", "account");
        model.addAttribute("accountUser", currentUser);
        model.addAttribute("invoices", invoices);
        model.addAttribute("paidPaymentCount", paymentHistoryService.countPaidOrdersByUserId(currentUser.getId()));
        model.addAttribute("paymentReviewCount", paymentHistoryService.countReviewsByUserId(currentUser.getId()));

        return "account/payments";
    }

    @GetMapping("/{orderId}")
    public String detail(@PathVariable String orderId,
                         Model model,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login";
        }

        PaymentInvoiceView invoice = paymentHistoryService
                .findPaidInvoiceDetail(currentUser.getId(), orderId)
                .orElse(null);

        if (invoice == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hóa đơn đã thanh toán.");
            return "redirect:/account/payments";
        }

        model.addAttribute("pageTitle", "Chi tiết hóa đơn - WebDuLich");
        model.addAttribute("activePage", "account");
        model.addAttribute("accountUser", currentUser);
        model.addAttribute("invoice", invoice);

        return "account/payment-detail";
    }

    @PostMapping("/{orderId}/review")
    public String saveReview(@PathVariable String orderId,
                             @RequestParam Long propertyId,
                             @RequestParam Integer rating,
                             @RequestParam(required = false) String content,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            paymentHistoryService.saveOrUpdateReview(
                    currentUser.getId(),
                    currentUser.getFullName(),
                    orderId,
                    propertyId,
                    rating,
                    content
            );

            redirectAttributes.addFlashAttribute("successMessage", "Đã lưu đánh giá tour.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/account/payments/" + orderId;
    }

    @PostMapping("/{orderId}/review/delete")
    public String deleteReview(@PathVariable String orderId,
                               @RequestParam Long propertyId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(session);

        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            paymentHistoryService.deleteReview(currentUser.getId(), orderId, propertyId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa đánh giá tour.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/account/payments/" + orderId;
    }

    private User getCurrentUser(HttpSession session) {
        Long currentUserId = getCurrentUserId(session);

        if (currentUserId == null) {
            return null;
        }

        return userRepository.findById(currentUserId).orElse(null);
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
