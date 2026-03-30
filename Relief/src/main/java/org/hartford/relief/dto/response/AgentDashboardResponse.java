package org.hartford.relief.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentDashboardResponse {

    private Long agentId;
    private String agentName;
    private String licenseNumber;
    private String region;

    // Policy stats
    private long totalPoliciesAssigned;
    private long pendingPolicies;
    private long approvedPolicies;
    private long rejectedPolicies;
    private long activePolicies;
    private long expiredPolicies;

    // Claim stats (on policies this agent underwrote)
    private long totalClaims;
    private long approvedClaims;
    private long pendingClaims;
    private long rejectedClaims;

    // Risk distribution: disasterType -> count
    private Map<String, Long> policiesByDisasterType;

    // Loss frequency: disasterType -> total estimated loss
    private Map<String, Double> lossFrequencyByDisasterType;

    // Approval ratio as percentage (approved / total * 100)
    private double approvalRatio;

    // Recent policies for quick view
    private List<PolicyResponse> recentPolicies;

    // Claims on agent's policies
    private List<ClaimResponse> recentClaims;
}
