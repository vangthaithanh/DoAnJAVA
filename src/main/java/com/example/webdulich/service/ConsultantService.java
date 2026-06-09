package com.example.webdulich.service;

import com.example.webdulich.entity.Agent;
import com.example.webdulich.entity.CustomItinerary;
import com.example.webdulich.entity.InquiryAssignment;
import com.example.webdulich.entity.User;
import com.example.webdulich.repository.AgentRepository;
import com.example.webdulich.repository.CustomItineraryRepository;
import com.example.webdulich.repository.InquiryAssignmentRepository;
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
    private final AgentRepository agentRepository;
    private final InquiryAssignmentRepository inquiryAssignmentRepository;
    private final InquiryService inquiryService;

    public ConsultantService(CustomItineraryRepository itineraryRepository,
                             UserRepository userRepository,
                             ChatService chatService,
                             AgentRepository agentRepository,
                             InquiryAssignmentRepository inquiryAssignmentRepository,
                             InquiryService inquiryService) {
        this.itineraryRepository = itineraryRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
        this.agentRepository = agentRepository;
        this.inquiryAssignmentRepository = inquiryAssignmentRepository;
        this.inquiryService = inquiryService;
    }

    @Transactional(readOnly = true)
    public List<CustomItinerary> getPendingItineraries() {
        return itineraryRepository.findByStatusOrderByCreatedAtDesc(CustomItinerary.STATUS_PENDING_REVIEW);
    }

    @Transactional(readOnly = true)
    public List<CustomItinerary> getAssignedItineraries(Long consultantUserId) {
        return itineraryRepository.findByAssignedAgentIdOrderByCreatedAtDesc(resolveAgentId(consultantUserId));
    }

    @Transactional(readOnly = true)
    public List<CustomItinerary> getItinerariesByStatus(Long consultantUserId, String status) {
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return getAssignedItineraries(consultantUserId);
        }
        return itineraryRepository.findByAssignedAgentIdAndStatusOrderByCreatedAtDesc(resolveAgentId(consultantUserId), status);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardStats(Long consultantUserId) {
        Long agentId = resolveAgentId(consultantUserId);
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", itineraryRepository.countByAssignedAgentId(agentId));
        stats.put("pending", itineraryRepository.countByAssignedAgentIdAndStatus(agentId, CustomItinerary.STATUS_PENDING_REVIEW));
        stats.put("advised", itineraryRepository.countByAssignedAgentIdAndStatus(agentId, CustomItinerary.STATUS_ADVISED));
        stats.put("approved", itineraryRepository.countByAssignedAgentIdAndStatus(agentId, CustomItinerary.STATUS_APPROVED));
        stats.put("rejected", itineraryRepository.countByAssignedAgentIdAndStatus(agentId, CustomItinerary.STATUS_REJECTED));
        stats.put("tourInquiries", inquiryAssignmentRepository.countByAgentId(agentId));
        stats.put("pendingTourInquiries", inquiryAssignmentRepository.countByAgentIdAndStatus(agentId, InquiryAssignment.STATUS_PENDING));
        return stats;
    }

    @Transactional
    public CustomItinerary adviseItinerary(Long id, String note, Long consultantUserId) {
        Long agentId = resolveAgentId(consultantUserId);
        CustomItinerary it = itineraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch trình"));

        if (it.getAssignedAgent() != null && it.getAssignedAgent().getId() != null
                && !it.getAssignedAgent().getId().equals(agentId)) {
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
    public CustomItinerary assignItinerary(Long id, Long consultantUserId) {
        CustomItinerary it = itineraryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch trình"));
        agentRepository.findById(resolveAgentId(consultantUserId)).ifPresent(it::setAssignedAgent);
        return itineraryRepository.save(it);
    }

    @Transactional(readOnly = true)
    public Optional<CustomItinerary> getItineraryById(Long id) {
        return itineraryRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<InquiryAssignment> getInquiryAssignments(Long consultantUserId) {
        return inquiryService.findAssignmentsForAgent(resolveAgentId(consultantUserId));
    }

    @Transactional(readOnly = true)
    public List<InquiryAssignment> getPendingInquiryAssignments(Long consultantUserId) {
        return inquiryService.findPendingAssignmentsForAgent(resolveAgentId(consultantUserId));
    }

    @Transactional
    public void handleInquiry(Long inquiryId, String note, Long consultantUserId) {
        inquiryService.markHandled(inquiryId, resolveAgentId(consultantUserId), note);
    }

    @Transactional(readOnly = true)
    public List<User> getAllConsultants() {
        return userRepository.findByRoleIgnoreCase("CONSULTANT");
    }

    @Transactional(readOnly = true)
    public Long resolveAgentId(Long consultantUserId) {
        User user = userRepository.findById(consultantUserId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản tư vấn viên."));
        Agent agent = agentRepository.findByEmailIgnoreCase(user.getEmail())
                .orElseThrow(() -> new IllegalStateException("Tài khoản tư vấn viên chưa được liên kết với hồ sơ nhân viên."));
        return agent.getId();
    }
}
