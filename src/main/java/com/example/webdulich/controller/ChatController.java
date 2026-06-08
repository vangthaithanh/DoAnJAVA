package com.example.webdulich.controller;

import com.example.webdulich.entity.Conversation;
import com.example.webdulich.entity.ConversationMessage;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.UserRepository;
import com.example.webdulich.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/account/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    private Long getUserId(HttpSession session) {
        Object id = session.getAttribute("currentUserId");
        return id != null ? ((Number) id).longValue() : null;
    }

    private String requireLogin(HttpSession session) {
        if (getUserId(session) == null) {
            return "redirect:/login";
        }
        return null;
    }

    @GetMapping
    public String chatList(HttpSession session, Model model) {
        String redirect = requireLogin(session);
        if (redirect != null) return redirect;

        Long userId = getUserId(session);
        List<Conversation> conversations = chatService.getConversationsForUser(userId);
        chatService.loadMessagesForConversations(conversations, userId);

        List<User> consultants = chatService.getAllConsultants();

        model.addAttribute("conversations", conversations);
        model.addAttribute("consultants", consultants);
        model.addAttribute("pageTitle", "Tin nhắn - WebDuLich");

        return "account/chat";
    }

    @GetMapping("/{id}")
    public String chatDetail(@PathVariable Long id,
                              HttpSession session,
                              Model model) {
        String redirect = requireLogin(session);
        if (redirect != null) return redirect;

        Long userId = getUserId(session);
        Optional<Conversation> opt = chatService.getConversation(id, userId);
        if (opt.isEmpty()) {
            return "redirect:/account/chat";
        }

        List<Conversation> conversationsForNav = chatService.getConversationsForUser(userId);
        chatService.loadMessagesForConversations(conversationsForNav, userId);

        Conversation conv = opt.get();
        List<ConversationMessage> messages = chatService.getMessages(id, userId);

        model.addAttribute("conversation", conv);
        model.addAttribute("conversationsForNav", conversationsForNav);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("pageTitle", "Tin nhắn - WebDuLich");

        return "account/chat-detail";
    }

    @PostMapping("/{id}/send")
    public String sendMessage(@PathVariable Long id,
                              @RequestParam String content,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        String redirect = requireLogin(session);
        if (redirect != null) return redirect;

        try {
            Long userId = getUserId(session);
            chatService.sendMessage(id, userId, content);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/account/chat/" + id;
    }

    @GetMapping("/{id}/messages")
    @ResponseBody
    public List<ConversationMessage> getNewMessages(@PathVariable Long id, HttpSession session) {
        Long userId = getUserId(session);
        if (userId == null) return List.of();
        return chatService.getMessages(id, userId);
    }

    @PostMapping("/new")
    public String newConversation(@RequestParam(required = false) Long consultantId,
                                  @RequestParam(required = false) Long itineraryId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        String redirect = requireLogin(session);
        if (redirect != null) return redirect;

        try {
            Long userId = getUserId(session);
            Conversation conv = chatService.getOrCreateConversation(userId, consultantId, itineraryId);
            return "redirect:/account/chat/" + conv.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/account/chat";
        }
    }
}
