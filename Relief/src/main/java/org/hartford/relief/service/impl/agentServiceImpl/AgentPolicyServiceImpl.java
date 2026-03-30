package org.hartford.relief.service.impl.agentServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AgentPremiumAdjustRequest;
import org.hartford.relief.dto.response.AgentDashboardResponse;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.dto.response.PolicyResponse;
import org.hartford.relief.entity.Agent;
import org.hartford.relief.entity.Claim;
import org.hartford.relief.entity.DisasterZone;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.exception.InvalidAmountException;
import org.hartford.relief.exception.InvalidStatusTransitionException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.exception.UnauthorizedAccessException;
import org.hartford.relief.repository.AgentRepository;
import org.hartford.relief.repository.ClaimRepository;
import org.hartford.relief.repository.PolicyRepository;
import org.hartford.relief.service.agentService.AgentPolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentPolicyServiceImpl implements AgentPolicyService {

    private final AgentRepository agentRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final org.hartford.relief.repository.PolicyDocumentRepository policyDocumentRepository;

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    private Agent getAgent(Long userId) {
        return agentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent", userId));
    }

    private Policy getOwnedPolicy(Long userId, Long policyId) {
        Agent agent = getAgent(userId);
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", policyId));
        if (policy.getAgent() == null || !policy.getAgent().getId().equals(agent.getId())) {
            throw new UnauthorizedAccessException("Policy does not belong to this agent.");
        }
        return policy;
    }

    // ──────────────────────────────────────────────
    // Policy views
    // ──────────────────────────────────────────────

    @Override
    public List<PolicyResponse> getMyPolicies(Long agentId) {
        Agent agent = getAgent(agentId);
        return policyRepository.findByAgentId(agent.getId())
                .stream()
                .map(this::mapPolicy)
                .collect(Collectors.toList());
    }

    @Override
    public PolicyResponse getMyPolicyById(Long agentId, Long policyId) {
        return mapPolicy(getOwnedPolicy(agentId, policyId));
    }

    @Override
    public List<PolicyResponse> getMyPoliciesByStatus(Long agentId, String status) {
        Agent agent = getAgent(agentId);
        return policyRepository.findByAgentId(agent.getId())
                .stream()
                .filter(p -> status.equalsIgnoreCase(p.getStatus()))
                .map(this::mapPolicy)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    // Underwriting actions
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public PolicyResponse adjustPremium(Long agentId, Long policyId, AgentPremiumAdjustRequest request) {
        Policy policy = getOwnedPolicy(agentId, policyId);

        // Can only adjust premium on PENDING or UNDER_REVIEW policies
        if (!("PENDING".equalsIgnoreCase(policy.getStatus()) || "UNDER_REVIEW".equalsIgnoreCase(policy.getStatus()))) {
            throw new InvalidStatusTransitionException(
                    "Premium can only be adjusted on PENDING or UNDER_REVIEW policies. Current status: " + policy.getStatus());
        }
        if (request.getAdjustedPremium() == null || request.getAdjustedPremium() <= 0) {
            throw new InvalidAmountException("Adjusted premium must be greater than zero.");
        }
        if (request.getAdjustedSumInsured() != null) {
            if (request.getAdjustedSumInsured() <= 0) {
                throw new InvalidAmountException("Adjusted sum insured must be greater than zero.");
            }
            policy.setSumInsured(request.getAdjustedSumInsured());
        }
        policy.setPremiumAmount(request.getAdjustedPremium());
        if (request.getRemarks() != null && !request.getRemarks().isBlank()) {
            policy.setRemarks(request.getRemarks());
        }
        // Once the agent sets the premium, the policy is considered approved — no further forwarding needed
        policy.setStatus("APPROVED");
        return mapPolicy(policyRepository.save(policy));
    }

    @Override
    public Double calculatePremium(Long agentId, Long policyId, Double sumInsured) {
        Policy policy = getOwnedPolicy(agentId, policyId);
        String policyType = policy.getPolicyType() != null ? policy.getPolicyType().toUpperCase() : "BASIC";
        double baseRate = switch (policyType) {
            case "STANDARD" -> 0.005;
            case "PREMIUM"  -> 0.008;
            default          -> 0.003;  // BASIC
        };
        double riskFactor = (policy.getDisasterZone() != null && policy.getDisasterZone().getRiskFactor() != null)
                ? policy.getDisasterZone().getRiskFactor()
                : 5.0;
        double multiplier = riskFactor / 10.0;
        return Math.round(sumInsured * baseRate * multiplier * 100.0) / 100.0;
    }

    // ──────────────────────────────────────────────
    // Dashboard
    // ──────────────────────────────────────────────

    @Override
    public AgentDashboardResponse getMyDashboard(Long agentId) {
        Agent agent = getAgent(agentId);

        List<Policy> policies = policyRepository.findByAgentId(agent.getId());
        List<Claim> claims    = claimRepository.findByPolicy_AgentId(agent.getId());

        long pending   = policies.stream().filter(p -> "PENDING".equalsIgnoreCase(p.getStatus())).count();
        long underReview = policies.stream().filter(p -> "UNDER_REVIEW".equalsIgnoreCase(p.getStatus())).count();
        long approved  = policies.stream().filter(p -> "APPROVED".equalsIgnoreCase(p.getStatus())).count();
        long rejected  = policies.stream().filter(p -> "REJECTED".equalsIgnoreCase(p.getStatus())).count();
        long active    = policies.stream().filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).count();

        long approvedClaims  = claims.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count();
        long pendingClaims   = claims.stream()
                .filter(c -> "FILED".equalsIgnoreCase(c.getStatus())
                        || "UNDER_REVIEW".equalsIgnoreCase(c.getStatus()))
                .count();
        long rejectedClaims  = claims.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count();

        // Risk distribution: disasterType -> number of policies
        Map<String, Long> policiesByDisasterType = policies.stream()
                .filter(p -> p.getDisasterType() != null)
                .collect(Collectors.groupingBy(p -> p.getDisasterType().toUpperCase(), Collectors.counting()));

        // Loss frequency: disasterType -> total estimated loss from claims
        Map<String, Double> lossFrequency = claims.stream()
                .filter(c -> c.getPolicy() != null && c.getPolicy().getDisasterType() != null
                        && c.getEstimatedLoss() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getPolicy().getDisasterType().toUpperCase(),
                        Collectors.summingDouble(Claim::getEstimatedLoss)
                ));

        // Approval ratio: (approved + active) out of all non-pending policies reviewed
        long reviewed = approved + active + rejected;
        double approvalRatio = reviewed > 0
                ? Math.round((double)(approved + active) / reviewed * 100.0 * 10) / 10.0
                : 0.0;

        // Recent 10 policies
        List<PolicyResponse> recentPolicies = policies.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .limit(10)
                .map(this::mapPolicy)
                .collect(Collectors.toList());

        // Recent 10 claims
        List<ClaimResponse> recentClaims = claims.stream()
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .limit(10)
                .map(this::mapClaim)
                .collect(Collectors.toList());

        return AgentDashboardResponse.builder()
                .agentId(agent.getId())
                .agentName(agent.getUser().getName())
                .licenseNumber(agent.getLicenseNumber())
                .region(agent.getRegion())
                .totalPoliciesAssigned(policies.size())
                .pendingPolicies(pending + underReview)
                .approvedPolicies(approved)
                .rejectedPolicies(rejected)
                .activePolicies(active)
                .totalClaims(claims.size())
                .approvedClaims(approvedClaims)
                .pendingClaims(pendingClaims)
                .rejectedClaims(rejectedClaims)
                .policiesByDisasterType(policiesByDisasterType)
                .lossFrequencyByDisasterType(lossFrequency)
                .approvalRatio(approvalRatio)
                .recentPolicies(recentPolicies)
                .recentClaims(recentClaims)
                .build();
    }

    // ──────────────────────────────────────────────
    // Mappers
    // ──────────────────────────────────────────────

    private PolicyResponse mapPolicy(Policy policy) {
        DisasterZone zone = policy.getDisasterZone();
        java.util.List<org.hartford.relief.dto.response.PolicyDocumentResponse> docs = policy.getDocuments().stream()
                .map(d -> org.hartford.relief.dto.response.PolicyDocumentResponse.builder()
                        .id(d.getId())
                        .policyId(d.getPolicy().getId())
                        .documentType(d.getDocumentType())
                        .fileUrl(d.getFileUrl())
                        .documentStatus(d.getDocumentStatus())
                        .agentRemarks(d.getAgentRemarks())
                        .uploadedAt(d.getUploadedAt())
                        .build())
                .collect(Collectors.toList());

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
                .disasterZoneId(zone != null ? zone.getId() : null)
                .disasterZoneName(zone != null ? zone.getZoneName() : null)
                .disasterZoneRiskFactor(zone != null ? zone.getRiskFactor() : null)
                .riskPoolDisasterType(policy.getRiskPool() != null ? policy.getRiskPool().getDisasterType() : null)
                .yearBuilt(policy.getYearBuilt())
                .constructionMaterial(policy.getConstructionMaterial())
                .previousClaimsHistory(policy.getPreviousClaimsHistory())
                .safetyFeatures(policy.getSafetyFeatures())
                .documents(docs)
                .build();
    }



    private ClaimResponse mapClaim(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .policyId(claim.getPolicy() != null ? claim.getPolicy().getId() : null)
                .policyNumber(claim.getPolicy() != null ? claim.getPolicy().getPolicyNumber() : null)
                .description(claim.getDescription())
                .estimatedLoss(claim.getEstimatedLoss())
                .approvedAmount(claim.getApprovedAmount())
                .status(claim.getStatus())
                .officerRemarks(claim.getOfficerRemarks())
                .filedDate(claim.getFiledDate())
                .resolvedDate(claim.getResolvedDate())
                .build();
    }

    @Override
    @Transactional
    public org.hartford.relief.dto.response.PolicyDocumentResponse reviewDocument(Long agentId, Long documentId, String status, String remarks) {
        Agent agent = getAgent(agentId);
        org.hartford.relief.entity.PolicyDocument document = policyDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("PolicyDocument", documentId));
        
        if (document.getPolicy().getAgent() == null || !document.getPolicy().getAgent().getId().equals(agent.getId())) {
            throw new UnauthorizedAccessException("This policy is not assigned to you.");
        }

        document.setDocumentStatus(status.toUpperCase());
        document.setAgentRemarks(remarks);
        
        document = policyDocumentRepository.save(document);

        return org.hartford.relief.dto.response.PolicyDocumentResponse.builder()
                .id(document.getId())
                .policyId(document.getPolicy().getId())
                .documentType(document.getDocumentType())
                .fileUrl(document.getFileUrl())
                .documentStatus(document.getDocumentStatus())
                .agentRemarks(document.getAgentRemarks())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
