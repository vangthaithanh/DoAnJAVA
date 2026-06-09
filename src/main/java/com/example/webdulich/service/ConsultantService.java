package com.example.webdulich.service;

import com.example.webdulich.entity.CustomItinerary;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.CustomItineraryRepository;
import com.example.webdulich.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConsultantService {

    private final CustomItineraryRepository itineraryRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    public ConsultantService(CustomItineraryRepository itineraryRepository,
                             UserRepository userRepository,
                             ChatService chatService) {
        this.itineraryRepository = itineraryRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
    }

    @Transactional(readOnly = true)
    public List<CustomItinerary> getPendingItineraries() {
        return itineraryRepository.findByStatusOrderByCreatedAtDesc(CustomItinerary.STATUS_PENDING_REVIEW);
    }

    @Transactional(readOnly = true)
    public List<CustomItinerary> getAssignedItineraries(Long agentUserId) {
        return itineraryRepository.findByAssignedAgentIdOrderByCreatedAtDesc(agentUserId);
    }

    @Transactional(readOnly = true)
    public List<CustomItinerary> getItinerariesByStatus(Long agentUserId, String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return itineraryRepository.findByAssignedAgentIdOrderByCreatedAtDesc(agentUserId);
        }
        return itineraryRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardStats(Long agentUserId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", itineraryRepository.countByAssignedAgentId(agentUserId));
        stats.put("pending", itineraryRepository.countByAssignedAgentIdAndStatus(agentUserId, CustomItinerary.STATUS_PENDING_REVIEW));
        stats.put("advised", itineraryRepository.countByAssignedAgentIdAndStatus(agentUserId, CustomItinerary.STATUS_ADVISED));
        stats.put("approved", itineraryRepository.countByAssignedAgentIdAndStatus(agentUserId, CustomItinerary.STATUS_APPROVED));
        stats.put("rejected", itineraryRepository.countByAssignedAgentIdAndStatus(agentUserId, CustomItinerary.STATUS_REJECTED));
        return stats;
    }

    @Transactional
    public CustomItinerary adviseItinerary(Long id, String note, Long consultantUserId) {
        CustomItinerary it = itineraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch trình"));

        if (it.getAssignedAgent() != null && it.getAssignedAgent().getId() != null
                && !it.getAssignedAgent().getId().equals(consultantUserId)) {
            throw new IllegalStateException("Yêu cầu này đã được tư vấn viên khác tiếp nhận");
        }

        it.setStatus(CustomItinerary.STATUS_ADVISED);
        it.setConsultantNote(note);

        CustomItinerary saved = itineraryRepository.save(it);
        chatService.getOrCreateConversation(saved.getUser().getId(), consultantUserId, saved.getId());
        return saved;
    }

    @Transactional
    public CustomItinerary approveItinerary(Long id) {
        CustomItinerary it = itineraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch trình"));
        if (!CustomItinerary.STATUS_ADVISED.equals(it.getStatus())) {
            throw new IllegalStateException("Chỉ có thể phê duyệt lịch trình đã được tư vấn");
        }
        it.setStatus(CustomItinerary.STATUS_APPROVED);
        return itineraryRepository.save(it);
    }

    @Transactional
    public CustomItinerary rejectItinerary(Long id, String reason) {
        CustomItinerary it = itineraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch trình"));
        it.setStatus(CustomItinerary.STATUS_REJECTED);
        it.setConsultantNote(reason);
        return itineraryRepository.save(it);
    }

    @Transactional
    public CustomItinerary assignItinerary(Long id, Long agentUserId) {
        CustomItinerary it = itineraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch trình"));
        Optional<User> userOpt = userRepository.findById(agentUserId);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            var agent = new com.example.webdulich.entity.Agent();
            agent.setId(u.getId());
            agent.setFullName(u.getFullName());
            agent.setEmail(u.getEmail());
            it.setAssignedAgent(agent);
        }
        return itineraryRepository.save(it);
    }

    @Transactional(readOnly = true)
    public Optional<CustomItinerary> getItineraryById(Long id) {
        return itineraryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<User> getAllConsultants() {
        return userRepository.findByRoleIgnoreCase("CONSULTANT");
    }
}
