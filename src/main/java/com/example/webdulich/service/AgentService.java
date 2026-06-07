package com.example.webdulich.service;

import com.example.webdulich.entity.Agent;
import com.example.webdulich.repository.AgentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgentService {

    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public List<Agent> findAll() {
        return agentRepository.findAll();
    }

    public Optional<Agent> findById(Long id) {
        return agentRepository.findById(id);
    }
}
