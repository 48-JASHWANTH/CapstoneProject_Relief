package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDecisionRequest {

    // APPROVED or REJECTED
    private String decision;

    // Approved payout amount (required when decision = APPROVED)
    private Double approvedAmount;

    // Officer's justification / remarks
    private String remarks;
}
