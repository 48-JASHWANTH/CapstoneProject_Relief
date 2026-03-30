package org.hartford.relief.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPremiumDecision {
    private Double suggestedCoverage;
    private Double suggestedPremium;
    private String underwritingReasoning;
}
