package org.hartford.relief.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskPoolResponse {

    private Long id;
    private String disasterType;
    private Double totalPremiumCollected;
    private Double totalClaimsPaid;
    private Double thresholdPercentage;
    private String poolStatus;
    private boolean criticalFlag;
    private int totalPolicies;
    private int totalClaims;
}
