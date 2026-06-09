package com.example.webdulich.controller;

import com.example.webdulich.entity.Conversation;
import com.example.webdulich.entity.ConversationMessage;
import com.example.webdulich.entity.CustomItinerary;
import com.example.webdulich.entity.User;
import com.example.webdulich.service.ChatService;
import com.example.webdulich.service.ConsultantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/consultant")
public class ConsultantController {

    private final ConsultantService consultantService;
    private final ChatService chatService;

    public ConsultantController(ConsultantService consultantService, ChatService chatService) {
        this.consultantService = consultantService;
        this.chatService = chatService;
    }

    private boolean isConsultant(HttpSession session) {
        return session.getAttribute("currentUserId") != null
                && "CONSULTANT".equalsIgnoreCase(String.valueOf(session.getAttribute("currentUserRole")));
    }

    private Long getUserId(HttpSession session) {
        Object id = session.getAttribute("currentUserId");
        return id != null ? ((Number) id).longValue() : null;
    }

    private String requireConsultant(HttpSession session) {
        if (!isConsultant(session)) {
            return "redirect:/";
        }
        return null;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        Long userId = getUserId(session);
        Long agentId = consultantService.resolveAgentId(userId);
        boolean itineraryConsultant = Long.valueOf(3L).equals(agentId);
        Map<String, Long> stats = consultantService.getDashboardStats(userId);
        List<CustomItinerary> pending = itineraryConsultant
                ? consultantService.getItinerariesByStatus(userId, CustomItinerary.STATUS_PENDING_REVIEW)
                : Collections.emptyList();
        List<CustomItinerary> myItineraries = itineraryConsultant
                ? consultantService.getAssignedItineraries(userId)
                : Collections.emptyList();
        var pendingInquiryAssignments = itineraryConsultant ? Collections.emptyList() : consultantService.getPendingInquiryAssignments(userId);
        var myInquiryAssignments = itineraryConsultant ? Collections.emptyList() : consultantService.getInquiryAssignments(userId);

        long unread = 0;
        List<Conversation> conversations = Collections.emptyList();

        try {
            unread = chatService.getTotalUnreadForUser(userId);
            conversations = chatService.getConversationsForConsultant(userId);
            chatService.loadMessagesForConversations(conversations, userId);
        } catch (DataAccessException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chức năng chat tư vấn chưa sẵn sàng trong cơ sở dữ liệu hiện tại. Vui lòng import lại bảng chat.");
        }

        model.addAttribute("stats", stats);
        model.addAttribute("unreadMessages", unread);
        model.addAttribute("pendingItineraries", pending);
        model.addAttribute("myItineraries", myItineraries);
        model.addAttribute("pendingInquiryAssignments", pendingInquiryAssignments);
        model.addAttribute("myInquiryAssignments", myInquiryAssignments);
        model.addAttribute("consultantAgentId", agentId);
        model.addAttribute("itineraryConsultant", itineraryConsultant);
        model.addAttribute("conversations", conversations);
        model.addAttribute("pageTitle", "Trang tư vấn viên - WebDuLich");

        return "consultant/dashboard";
    }

    @GetMapping("/requests")
    public String requests(@RequestParam(required = false, defaultValue = "ALL") String status,
                          HttpSession session, Model model) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        Long userId = getUserId(session);
        List<CustomItinerary> itineraries;

        if ("PENDING_REVIEW".equals(status)) {
            itineraries = consultantService.getItinerariesByStatus(userId, CustomItinerary.STATUS_PENDING_REVIEW);
        } else if ("ALL".equalsIgnoreCase(status)) {
            itineraries = consultantService.getAssignedItineraries(userId);
        } else {
            itineraries = consultantService.getItinerariesByStatus(userId, status);
        }

        model.addAttribute("itineraries", itineraries);
        model.addAttribute("currentStatus", status);
        model.addAttribute("pageTitle", "Yêu cầu - Tư vấn viên");

        return "consultant/requests";
    }

    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable Long id, HttpSession session, Model model) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        Optional<CustomItinerary> opt = consultantService.getItineraryById(id);
        if (opt.isEmpty()) {
            return "redirect:/consultant/requests";
        }

        model.addAttribute("itinerary", opt.get());
        model.addAttribute("pageTitle", "Chi tiết yêu cầu - Tư vấn viên");

        return "consultant/request-detail";
    }

    @PostMapping("/requests/{id}/advise")
    public String advise(@PathVariable Long id,
                         @RequestParam(required = false) String note,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        try {
            Long userId = getUserId(session);
            consultantService.adviseItinerary(id, note, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã đánh dấu tư vấn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/consultant";
    }

    @PostMapping("/requests/{id}/approve")
    public String approve(@PathVariable Long id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        try {
            consultantService.approveItinerary(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã phê duyệt lịch trình!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/consultant";
    }

    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(required = false) String reason,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        try {
            consultantService.rejectItinerary(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối lịch trình!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/consultant";
    }

    @PostMapping("/inquiries/{id}/handle")
    public String handleInquiry(@PathVariable Long id,
                                @RequestParam(required = false) String note,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        try {
            consultantService.handleInquiry(id, note, getUserId(session));
            redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận xử lý yêu cầu tư vấn.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/consultant";
    }

    @GetMapping("/chat")
    public String chatList(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        Long userId = getUserId(session);
        try {
            List<Conversation> conversations = chatService.getConversationsForConsultant(userId);
            chatService.loadMessagesForConversations(conversations, userId);
            model.addAttribute("conversations", conversations);
        } catch (DataAccessException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chức năng chat tư vấn chưa sẵn sàng trong cơ sở dữ liệu hiện tại. Vui lòng import lại bảng chat.");
            return "redirect:/consultant";
        }

        model.addAttribute("pageTitle", "Trò chuyện - Tư vấn viên");

        return "consultant/chat";
    }

    @GetMapping("/chat/{id}")
    public String chatDetail(@PathVariable Long id,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        Long userId = getUserId(session);
        try {
            Optional<Conversation> opt = chatService.getConversation(id, userId);
            if (opt.isEmpty()) {
                return "redirect:/consultant/chat";
            }

            Conversation conv = opt.get();
            List<ConversationMessage> messages = chatService.getMessages(id, userId);

            model.addAttribute("conversation", conv);
            model.addAttribute("messages", messages);
            model.addAttribute("currentUserId", userId);
            model.addAttribute("pageTitle", "Trò chuyện - Tư vấn viên");

            return "consultant/chat-detail";
        } catch (DataAccessException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chức năng chat tư vấn chưa sẵn sàng trong cơ sở dữ liệu hiện tại. Vui lòng import lại bảng chat.");
            return "redirect:/consultant";
        }
    }

    @PostMapping("/chat/{id}/send")
    public String sendMessage(@PathVariable Long id,
                              @RequestParam String content,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String redirect = requireConsultant(session);
        if (redirect != null) return redirect;

        try {
            Long userId = getUserId(session);
            chatService.sendMessage(id, userId, content);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/consultant/chat/" + id;
    }

    @GetMapping("/chat/{id}/messages")
    @ResponseBody
    public List<ConversationMessage> getNewMessages(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return List.of();
        return chatService.getMessages(id, userId);
    }
}
