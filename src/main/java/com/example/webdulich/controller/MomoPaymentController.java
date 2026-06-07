package com.example.webdulich.controller;

import com.example.webdulich.entity.PaymentOrder;
import com.example.webdulich.entity.Property;
import com.example.webdulich.service.MomoPaymentService;
import com.example.webdulich.service.PropertyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/payment/momo")
public class MomoPaymentController {

    private static final String CART_SESSION_KEY = "cartTourIds";

    private final PropertyService propertyService;
    private final MomoPaymentService momoPaymentService;

    public MomoPaymentController(PropertyService propertyService,
                                 MomoPaymentService momoPaymentService) {
        this.propertyService = propertyService;
        this.momoPaymentService = momoPaymentService;
    }

    @PostMapping("/create")
    public String createPayment(HttpSession session, RedirectAttributes redirectAttributes) {
        List<Property> cartTours = getCartTours(session);

        if (cartTours.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ tour đang trống, không thể thanh toán MoMo.");
            return "redirect:/cart";
        }

        try {
            Long userId = getCurrentUserId(session);
            String userName = getCurrentUserName(session);

            if (momoPaymentService.isDemoMode()) {
                PaymentOrder paymentOrder = momoPaymentService.createDemoPayment(cartTours, userId, userName);
                return "redirect:/payment/momo/demo?orderId=" + paymentOrder.getOrderId();
            }

            PaymentOrder paymentOrder = momoPaymentService.createMomoPayment(cartTours, userId, userName);
            return "redirect:" + paymentOrder.getPayUrl();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/demo")
    public String demoPage(@RequestParam String orderId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        PaymentOrder paymentOrder = momoPaymentService.findByOrderId(orderId).orElse(null);

        if (paymentOrder == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn thanh toán demo MoMo.");
            return "redirect:/cart";
        }

        model.addAttribute("pageTitle", "Cổng thanh toán MoMo Demo");
        model.addAttribute("paymentOrder", paymentOrder);
        model.addAttribute("demoAccounts", momoPaymentService.getDemoAccounts());

        return "payment/momo-demo";
    }

    @PostMapping("/demo/pay")
    public String demoPay(@RequestParam String orderId,
                          @RequestParam String phone,
                          @RequestParam String password,
                          HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            momoPaymentService.markDemoPaymentPaid(orderId, phone, password);
            session.removeAttribute(CART_SESSION_KEY);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán demo MoMo thành công.");
            return "redirect:/payment/momo/demo/result?orderId=" + orderId;
        } catch (Exception ex) {
            PaymentOrder paymentOrder = momoPaymentService.findByOrderId(orderId).orElse(null);

            if (paymentOrder == null) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:/cart";
            }

            model.addAttribute("pageTitle", "Cổng thanh toán MoMo Demo");
            model.addAttribute("paymentOrder", paymentOrder);
            model.addAttribute("demoAccounts", momoPaymentService.getDemoAccounts());
            model.addAttribute("errorMessage", ex.getMessage());

            return "payment/momo-demo";
        }
    }

    @PostMapping("/demo/cancel")
    public String demoCancel(@RequestParam String orderId,
                             RedirectAttributes redirectAttributes) {
        try {
            momoPaymentService.markDemoPaymentCanceled(orderId);
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã hủy thanh toán MoMo.");
            return "redirect:/payment/momo/demo/result?orderId=" + orderId;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/demo/result")
    public String demoResult(@RequestParam String orderId,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        PaymentOrder paymentOrder = momoPaymentService.findByOrderId(orderId).orElse(null);

        if (paymentOrder == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy kết quả thanh toán demo MoMo.");
            return "redirect:/cart";
        }

        boolean paid = "PAID".equalsIgnoreCase(paymentOrder.getStatus());

        model.addAttribute("pageTitle", "Kết quả thanh toán MoMo Demo - WebDuLich");
        model.addAttribute("activePage", "cart");
        model.addAttribute("paymentOrder", paymentOrder);
        model.addAttribute("paid", paid);
        model.addAttribute("validSignature", true);
        model.addAttribute("momoParams", new LinkedHashMap<String, String>());
        model.addAttribute("message", paymentOrder.getMessage());
        model.addAttribute("amount", paymentOrder.getAmount());

        return "payment/result";
    }

    @GetMapping("/return")
    public String paymentReturn(@RequestParam Map<String, String> params,
                                HttpSession session,
                                Model model) {
        PaymentOrder paymentOrder = null;
        boolean paid = false;
        boolean validSignature = false;
        String orderId = params.get("orderId");
        String message = params.getOrDefault("message", "Không nhận được thông báo từ MoMo.");

        try {
            validSignature = momoPaymentService.verifyResultSignature(params);
            paymentOrder = momoPaymentService.updatePaymentResult(params, false);
            paid = validSignature && paymentOrder.getResultCode() != null && paymentOrder.getResultCode() == 0;

            if (paid) {
                session.removeAttribute(CART_SESSION_KEY);
            }
        } catch (Exception ex) {
            if (orderId != null && !orderId.isBlank()) {
                paymentOrder = momoPaymentService.findByOrderId(orderId).orElse(null);
            }
            message = ex.getMessage();
        }

        model.addAttribute("pageTitle", "Kết quả thanh toán MoMo - WebDuLich");
        model.addAttribute("activePage", "cart");
        model.addAttribute("paymentOrder", paymentOrder);
        model.addAttribute("paid", paid);
        model.addAttribute("validSignature", validSignature);
        model.addAttribute("momoParams", params);
        model.addAttribute("message", message);
        model.addAttribute("amount", parseAmount(params.get("amount")));

        return "payment/result";
    }

    @PostMapping("/ipn")
    public ResponseEntity<Void> ipn(@RequestBody Map<String, Object> payload) {
        Map<String, String> params = convertToStringMap(payload);

        try {
            momoPaymentService.updatePaymentResult(params, true);
        } catch (Exception ignored) {
            // IPN cần phản hồi nhanh cho MoMo.
        }

        return ResponseEntity.noContent().build();
    }

    @SuppressWarnings("unchecked")
    private List<Long> getCartTourIds(HttpSession session) {
        Object value = session.getAttribute(CART_SESSION_KEY);

        if (value instanceof List<?>) {
            return new ArrayList<>((List<Long>) value);
        }

        return new ArrayList<>();
    }

    private List<Property> getCartTours(HttpSession session) {
        return getCartTourIds(session).stream()
                .distinct()
                .map(propertyService::findById)
                .flatMap(java.util.Optional::stream)
                .toList();
    }

    private Long getCurrentUserId(HttpSession session) {
        Object value = session.getAttribute("currentUserId");

        if (value instanceof Number number) {
            return number.longValue();
        }

        if (value instanceof String text && !text.isBlank()) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

    private String getCurrentUserName(HttpSession session) {
        Object value = session.getAttribute("currentUserName");
        return value == null ? null : value.toString();
    }

    private Map<String, String> convertToStringMap(Map<String, Object> payload) {
        Map<String, String> result = new LinkedHashMap<>();
        payload.forEach((key, value) -> result.put(key, value == null ? "" : String.valueOf(value)));
        return result;
    }

    private BigDecimal parseAmount(String amount) {
        try {
            return amount == null || amount.isBlank() ? BigDecimal.ZERO : new BigDecimal(amount);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}