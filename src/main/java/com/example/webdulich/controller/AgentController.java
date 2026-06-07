package com.example.webdulich.controller;

import com.example.webdulich.entity.Agent;
import com.example.webdulich.service.AgentService;
import com.example.webdulich.service.PropertyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agents")
public class AgentController {

    private final AgentService agentService;
    private final PropertyService propertyService;

    public AgentController(AgentService agentService, PropertyService propertyService) {
        this.agentService = agentService;
        this.propertyService = propertyService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "Tư vấn viên - WebDuLich");
        model.addAttribute("activePage", "agents");
        model.addAttribute("agents", agentService.findAll());
        return "agents/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Agent agent = agentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tư vấn viên id = " + id));

        model.addAttribute("pageTitle", agent.getFullName() + " - WebDuLich");
        model.addAttribute("activePage", "agents");
        model.addAttribute("agent", agent);
        model.addAttribute("properties", propertyService.findByAgent(id));
        model.addAttribute("tours", propertyService.findByAgent(id));
        return "agents/detail";
    }
}
