package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentPremiumAdjustRequest {

    // Updated coverage amount set by the agent/underwriter
    private Double adjustedSumInsured;

    // New premium amount recommended by underwriter
    private Double adjustedPremium;

    // Reason / underwriting notes
    private String remarks;
}
