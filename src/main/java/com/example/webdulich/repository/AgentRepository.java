package com.example.webdulich.repository;

import com.example.webdulich.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByEmailIgnoreCase(String email);
}
