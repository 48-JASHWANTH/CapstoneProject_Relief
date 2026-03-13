package org.hartford.relief.service.impl.adminServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AgentRequest;
import org.hartford.relief.dto.response.AgentResponse;
import org.hartford.relief.entity.Agent;
import org.hartford.relief.entity.User;
import org.hartford.relief.exception.DuplicateResourceException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.AgentRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.adminService.AdminAgentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAgentServiceImpl implements AdminAgentService {

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AgentResponse createAgent(AgentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        if (agentRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new DuplicateResourceException("Agent profile already exists for user id: " + request.getUserId());
        }
        if (agentRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License number already registered: " + request.getLicenseNumber());
        }

        Agent agent = Agent.builder()
                .user(user)
                .licenseNumber(request.getLicenseNumber())
                .region(request.getRegion())
                .build();
        return mapToResponse(agentRepository.save(agent));
    }

    @Override
    public List<AgentResponse> getAllAgents() {
        return agentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AgentResponse getAgentById(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent", id));
        return mapToResponse(agent);
    }

    @Override
    @Transactional
    public AgentResponse updateAgent(Long id, AgentRequest request) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent", id));

        if (!agent.getLicenseNumber().equals(request.getLicenseNumber())
                && agentRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("License number already registered: " + request.getLicenseNumber());
        }
        agent.setLicenseNumber(request.getLicenseNumber());
        agent.setRegion(request.getRegion());
        return mapToResponse(agentRepository.save(agent));
    }

    @Override
    @Transactional
    public void deleteAgent(Long id) {
        if (!agentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agent", id);
        }
        agentRepository.deleteById(id);
    }

    @Override
    public List<AgentResponse> getAgentsByRegion(String region) {
        return agentRepository.findByRegion(region)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AgentResponse mapToResponse(Agent agent) {
        return AgentResponse.builder()
                .id(agent.getId())
                .userId(agent.getUser().getId())
                .userName(agent.getUser().getName())
                .userEmail(agent.getUser().getEmail())
                .licenseNumber(agent.getLicenseNumber())
                .region(agent.getRegion())
                .totalPolicies(agent.getPolicies() != null ? agent.getPolicies().size() : 0)
                .build();
    }
}
