package com.example.webdulich.service;

import com.example.webdulich.entity.Agent;
import com.example.webdulich.entity.ContactMessage;
import com.example.webdulich.entity.Inquiry;
import com.example.webdulich.entity.InquiryAssignment;
import com.example.webdulich.entity.Property;
import com.example.webdulich.repository.AgentRepository;
import com.example.webdulich.repository.InquiryAssignmentRepository;
import com.example.webdulich.repository.InquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAssignmentRepository inquiryAssignmentRepository;
    private final AgentRepository agentRepository;

    public InquiryService(InquiryRepository inquiryRepository,
                          InquiryAssignmentRepository inquiryAssignmentRepository,
                          AgentRepository agentRepository) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryAssignmentRepository = inquiryAssignmentRepository;
        this.agentRepository = agentRepository;
    }

    @Transactional
    public Inquiry save(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public Inquiry createTourInquiry(Inquiry inquiry, Property property) {
        Agent assignedAgent = resolveAgentForProperty(property);
        inquiry.setId(null);
        inquiry.setProperty(property);
        inquiry.setSource(Inquiry.SOURCE_TOUR);
        inquiry.setStatus(Inquiry.STATUS_PENDING);
        inquiry.setAssignedAgent(assignedAgent);

        Inquiry saved = inquiryRepository.save(inquiry);
        createAssignment(saved, assignedAgent);
        return saved;
    }

    @Transactional
    public Inquiry createContactInquiry(ContactMessage contactMessage) {
        Inquiry inquiry = new Inquiry();
        inquiry.setFullName(contactMessage.getName());
        inquiry.setEmail(contactMessage.getEmail());
        inquiry.setPhone(contactMessage.getPhone());
        inquiry.setMessage(contactMessage.getMessage());
        inquiry.setSource(Inquiry.SOURCE_CONTACT);
        inquiry.setStatus(Inquiry.STATUS_PENDING);

        Inquiry saved = inquiryRepository.save(inquiry);
        for (Agent agent : agentRepository.findAll()) {
            if (!Long.valueOf(3L).equals(agent.getId())) {
                createAssignment(saved, agent);
            }
        }
        return saved;
    }

    @Transactional
    public Inquiry markHandled(Long inquiryId, Long agentId, String note) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu tư vấn."));
        Agent handler = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tư vấn viên."));

        LocalDateTime now = LocalDateTime.now();
        inquiry.setStatus(Inquiry.STATUS_HANDLED);
        inquiry.setHandledByAgent(handler);
        inquiry.setHandledAt(now);
        inquiry.setConsultantNote(note);

        List<InquiryAssignment> assignments = inquiryAssignmentRepository.findByInquiryId(inquiryId);
        boolean hasAssignment = false;
        for (InquiryAssignment assignment : assignments) {
            boolean isHandler = assignment.getAgent() != null && handler.getId().equals(assignment.getAgent().getId());
            assignment.setStatus(isHandler ? InquiryAssignment.STATUS_HANDLED : InquiryAssignment.STATUS_COLLEAGUE_HANDLED);
            assignment.setHandledAt(now);
            inquiryAssignmentRepository.save(assignment);
            hasAssignment = hasAssignment || isHandler;
        }

        if (!hasAssignment) {
            InquiryAssignment assignment = createAssignment(inquiry, handler);
            assignment.setStatus(InquiryAssignment.STATUS_HANDLED);
            assignment.setHandledAt(now);
            inquiryAssignmentRepository.save(assignment);
        }

        return inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public List<InquiryAssignment> findAssignmentsForAgent(Long agentId) {
        return inquiryAssignmentRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
    }

    @Transactional(readOnly = true)
    public List<InquiryAssignment> findPendingAssignmentsForAgent(Long agentId) {
        return inquiryAssignmentRepository.findByAgentIdAndStatusOrderByCreatedAtDesc(agentId, InquiryAssignment.STATUS_PENDING);
    }

    @Transactional(readOnly = true)
    public List<Inquiry> findByCustomerEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }
        return inquiryRepository.findByEmailIgnoreCaseOrderByCreatedAtDesc(email.trim());
    }

    @Transactional(readOnly = true)
    public long countByCustomerEmail(String email) {
        if (email == null || email.isBlank()) {
            return 0;
        }
        return inquiryRepository.countByEmailIgnoreCase(email.trim());
    }

    private InquiryAssignment createAssignment(Inquiry inquiry, Agent agent) {
        if (agent == null) {
            return null;
        }
        return inquiryAssignmentRepository.findByInquiryIdAndAgentId(inquiry.getId(), agent.getId())
                .orElseGet(() -> {
                    InquiryAssignment assignment = new InquiryAssignment();
                    assignment.setInquiry(inquiry);
                    assignment.setAgent(agent);
                    assignment.setStatus(InquiryAssignment.STATUS_PENDING);
                    return inquiryAssignmentRepository.save(assignment);
                });
    }

    private Agent resolveAgentForProperty(Property property) {
        if (property == null) {
            return null;
        }
        if (property.getAgent() != null) {
            return property.getAgent();
        }

        String destinationKey = normalize(property.getModelDestinationKey());
        String city = normalize(property.getCity());
        String type = normalize(property.getType());

        Long agentId = null;
        if ("da_lat".equals(destinationKey) || containsAny(city, "đà lạt", "da lat") || type.contains("tây nguyên")) {
            agentId = 4L;
        } else if ("phan_thiet".equals(destinationKey) || containsAny(city, "phan thiết", "phan thiet", "mũi né", "mui ne")) {
            agentId = 5L;
        } else if ("vung_tau".equals(destinationKey) || containsAny(city, "vũng tàu", "vung tau", "hồ tràm", "ho tram")) {
            agentId = 6L;
        } else if (type.contains("miền bắc") || type.contains("mien bac")) {
            agentId = 2L;
        } else if (type.contains("miền trung") || type.contains("mien trung")) {
            agentId = 1L;
        } else if (type.contains("miền nam") || type.contains("mien nam") || type.contains("biển đảo") || type.contains("bien dao")) {
            agentId = 1L;
        }

        return agentId == null ? null : agentRepository.findById(agentId).orElse(null);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
