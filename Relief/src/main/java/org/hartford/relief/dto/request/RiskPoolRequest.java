package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskPoolRequest {

    private String disasterType;
    private Double totalPremiumCollected;
    private Double totalClaimsPaid;
    private Double thresholdPercentage;
    private String poolStatus;
}
