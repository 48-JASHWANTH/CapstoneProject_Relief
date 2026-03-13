package org.hartford.relief.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimsOfficerDashboardResponse {

    // Claim counts by status
    private long totalClaims;
    private long filedClaims;
    private long underReviewClaims;
    private long approvedClaims;
    private long rejectedClaims;
    private long paidClaims;

    // Financial overview
    private Double totalApprovedAmount;
    private Double totalPaidAmount;

    // Claims grouped by disaster type
    private Map<String, Long> claimsByDisasterType;

    // Claims grouped by status
    private Map<String, Long> claimsByStatus;

    // Risk pool financial health snapshot
    private List<RiskPoolResponse> riskPoolSnapshot;

    // Recent 10 claims needing attention (FILED or SURVEY_ASSIGNED)
    private List<ClaimResponse> pendingAttentionClaims;
}
