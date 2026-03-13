package org.hartford.relief.dto.response;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalUsers;
    private long totalAgents;
    private long totalPolicies;
    private long totalClaims;
    private long totalPayments;
    private long totalDisasterZones;
    private long totalRiskPools;

    private long activePolicies;
    private long pendingPolicies;
    private long approvedClaims;
    private long pendingClaims;

    private Map<String, Long> policiesByDisasterType;
    private Map<String, Long> claimsByStatus;
    private Map<String, Double> riskPoolSummary;

    private long criticalRiskPools;
}
