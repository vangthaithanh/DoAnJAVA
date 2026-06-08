package com.example.webdulich.service;

import com.example.webdulich.entity.Conversation;
import com.example.webdulich.entity.ConversationMessage;
import com.example.webdulich.entity.CustomItinerary;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.ConversationMessageRepository;
import com.example.webdulich.repository.ConversationRepository;
import com.example.webdulich.repository.CustomItineraryRepository;
import com.example.webdulich.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CustomItineraryRepository itineraryRepository;

    public ChatService(ConversationRepository conversationRepository,
                       ConversationMessageRepository messageRepository,
                       UserRepository userRepository,
                       CustomItineraryRepository itineraryRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.itineraryRepository = itineraryRepository;
    }

    @Transactional
    public Conversation getOrCreateConversation(Long userId, Long agentUserId, Long itineraryId) {
        if (userId == null) {
            throw new IllegalArgumentException("Bạn cần đăng nhập để bắt đầu trò chuyện");
        }

        User customer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));

        CustomItinerary itinerary = null;
        if (itineraryId != null) {
            itinerary = itineraryRepository.findByIdAndUserId(itineraryId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu tư vấn của bạn"));

            Optional<Conversation> existingByItinerary = conversationRepository.findByItineraryIdAndStatus(
                    itineraryId, Conversation.STATUS_ACTIVE);
            if (existingByItinerary.isPresent()) {
                Conversation existingConversation = existingByItinerary.get();
                if (!existingConversation.getUser().getId().equals(userId)) {
                    throw new IllegalArgumentException("Bạn không có quyền truy cập cuộc trò chuyện này");
                }
                if (agentUserId != null
                        && existingConversation.getAgentUser() != null
                        && !agentUserId.equals(existingConversation.getAgentUser().getId())) {
                    throw new IllegalArgumentException("Bạn chỉ có thể trò chuyện với tư vấn viên phụ trách yêu cầu này");
                }
                return existingConversation;
            }
        }

        if (agentUserId == null) {
            throw new IllegalArgumentException("Thiếu thông tin tư vấn viên phụ trách");
        }

        User consultant = userRepository.findById(agentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tư vấn viên"));

        Optional<Conversation> existing = itineraryId != null
                ? conversationRepository.findByUserIdAndAgentUserIdAndItineraryIdAndStatus(userId, agentUserId, itineraryId, Conversation.STATUS_ACTIVE)
                : conversationRepository.findByUserIdAndAgentUserIdAndStatus(userId, agentUserId, Conversation.STATUS_ACTIVE);

        if (existing.isPresent()) {
            return existing.get();
        }

        Conversation conv = new Conversation();
        conv.setUser(customer);
        conv.setAgentUser(consultant);
        conv.setItinerary(itinerary);
        conv.setStatus(Conversation.STATUS_ACTIVE);
        conv.setCreatedAt(LocalDateTime.now());
        conv.setUpdatedAt(LocalDateTime.now());
        return conversationRepository.save(conv);
    }

    @Transactional(readOnly = true)
    public Optional<Conversation> getConversation(Long id, Long userId) {
        return conversationRepository.findById(id)
                .filter(c -> {
                    boolean isUser = c.getUser() != null && c.getUser().getId().equals(userId);
                    boolean isAgent = c.getAgentUser() != null && c.getAgentUser().getId().equals(userId);
                    return isUser || isAgent;
                });
    }

    @Transactional(readOnly = true)
    public List<Conversation> getConversationsForUser(Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Conversation> getConversationsForConsultant(Long consultantUserId) {
        return conversationRepository.findByAgentUserIdOrderByUpdatedAtDesc(consultantUserId);
    }

    @Transactional
    public ConversationMessage sendMessage(Long conversationId, Long senderId, String content) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc trò chuyện"));

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung tin nhắn không được để trống");
        }

        if (!conv.getUser().getId().equals(senderId) && !conv.getAgentUser().getId().equals(senderId)) {
            throw new SecurityException("Bạn không có quyền gửi tin nhắn trong cuộc trò chuyện này");
        }

        ConversationMessage msg = new ConversationMessage();
        msg.setConversation(conv);
        msg.setSender(userRepository.findById(senderId).orElseThrow());
        msg.setContent(content.trim());
        msg.setRead(false);
        msg.setCreatedAt(LocalDateTime.now());

        ConversationMessage saved = messageRepository.save(msg);

        conv.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conv);

        return saved;
    }

    @Transactional
    public List<ConversationMessage> getMessages(Long conversationId, Long viewerId) {
        messageRepository.markAsRead(conversationId, viewerId);
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long conversationId, Long viewerId) {
        return messageRepository.countByConversationIdAndSenderIdNotAndIsReadFalse(conversationId, viewerId);
    }

    @Transactional
    public long getTotalUnreadForUser(Long userId) {
        List<Conversation> convs = conversationRepository.findAllByParticipant(userId);
        long total = 0;
        for (Conversation c : convs) {
            total += getUnreadCount(c.getId(), userId);
        }
        return total;
    }

    @Transactional
    public void loadMessagesForConversations(List<Conversation> conversations, Long currentUserId) {
        for (Conversation conv : conversations) {
            List<ConversationMessage> msgs = messageRepository
                    .findByConversationIdOrderByCreatedAtAsc(conv.getId());
            conv.setMessages(msgs);
            conv.setUnreadCount(getUnreadCount(conv.getId(), currentUserId));
        }
    }

    @Transactional(readOnly = true)
    public List<User> getAllConsultants() {
        return userRepository.findByRoleIgnoreCase("CONSULTANT");
    }
}
