package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRequest {

    private Long policyId;
    private String description;
    private Double estimatedLoss;
    private java.time.LocalDate incidentDate;
    private String damageType;
}
