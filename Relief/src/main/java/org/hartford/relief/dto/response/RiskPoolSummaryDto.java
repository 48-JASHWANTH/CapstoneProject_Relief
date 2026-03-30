package org.hartford.relief.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskPoolSummaryDto {
    private String disasterType;
    private Double totalPremiumCollected;
    private Double totalClaimsPaid;
    private String poolStatus;
    private boolean criticalFlag;
}
