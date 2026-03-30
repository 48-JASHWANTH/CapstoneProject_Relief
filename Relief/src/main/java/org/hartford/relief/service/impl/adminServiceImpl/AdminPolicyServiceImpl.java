package org.hartford.relief.service.impl.adminServiceImpl;

import org.hartford.relief.dto.request.AssignAgentRequest;
import org.hartford.relief.dto.request.PolicyApprovalRequest;
import org.hartford.relief.dto.response.PolicyResponse;
import org.hartford.relief.entity.Agent;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.exception.BadRequestException;
import org.hartford.relief.exception.InvalidStatusTransitionException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.AgentRepository;
import org.hartford.relief.repository.PolicyRepository;
import org.hartford.relief.service.adminService.AdminPolicyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminPolicyServiceImpl implements AdminPolicyService {

    private final PolicyRepository policyRepository;
    private final AgentRepository agentRepository;

    public AdminPolicyServiceImpl(PolicyRepository policyRepository, AgentRepository agentRepository) {
        this.policyRepository = policyRepository;
        this.agentRepository = agentRepository;
    }

    public List<PolicyResponse> getAllPolicies() {
        return policyRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PolicyResponse getPolicyById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", id));
        return mapToResponse(policy);
    }

    public PolicyResponse approveOrRejectPolicy(Long id, PolicyApprovalRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", id));

        String newStatus = request.getStatus().toUpperCase();
        if (!newStatus.equals("APPROVED") && !newStatus.equals("REJECTED")) {
            throw new BadRequestException("Status must be APPROVED or REJECTED.");
        }
        if (!policy.getStatus().equalsIgnoreCase("PENDING")
                && !policy.getStatus().equalsIgnoreCase("UNDER_REVIEW")) {
            throw new InvalidStatusTransitionException(
                    "Only PENDING or UNDER_REVIEW policies can be approved or rejected. Current status: " + policy.getStatus());
        }
        policy.setStatus(newStatus);
        if (newStatus.equals("APPROVED")) {
            policy.setNextPremiumDueDate(java.time.LocalDate.now());
        }
        if (request.getRemarks() != null) {
            policy.setRemarks(request.getRemarks());
        }
        return mapToResponse(policyRepository.save(policy));
    }

    public PolicyResponse assignAgentToPolicy(Long policyId, AssignAgentRequest request) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", policyId));

        Agent agent = agentRepository.findById(request.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent", request.getAgentId()));

        policy.setAgent(agent);
        return mapToResponse(policyRepository.save(policy));
    }

    public List<PolicyResponse> getPoliciesByStatus(String status) {
        return policyRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PolicyResponse> getPoliciesByDisasterType(String disasterType) {
        return policyRepository.findByDisasterType(disasterType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PolicyResponse mapToResponse(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .userId(policy.getUser() != null ? policy.getUser().getId() : null)
                .userName(policy.getUser() != null ? policy.getUser().getName() : null)
                .agentId(policy.getAgent() != null ? policy.getAgent().getId() : null)
                .agentName(policy.getAgent() != null ? policy.getAgent().getUser().getName() : null)
                .disasterType(policy.getDisasterType())
                .policyType(policy.getPolicyType())
                .propertyAddress(policy.getPropertyAddress())
                .propertyValue(policy.getPropertyValue())
                .sumInsured(policy.getSumInsured())
                .premiumAmount(policy.getPremiumAmount())
                .status(policy.getStatus())
                .remarks(policy.getRemarks())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .nextPremiumDueDate(policy.getNextPremiumDueDate())
                .region(policy.getRegion())
                .tenure(policy.getTenure())
                .disasterZoneId(policy.getDisasterZone() != null ? policy.getDisasterZone().getId() : null)
                .disasterZoneName(policy.getDisasterZone() != null ? policy.getDisasterZone().getZoneName() : null)
                .disasterZoneRiskFactor(policy.getDisasterZone() != null ? policy.getDisasterZone().getRiskFactor() : null)
                .riskPoolDisasterType(policy.getRiskPool() != null ? policy.getRiskPool().getDisasterType() : null)
                .build();
    }
}
