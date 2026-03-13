package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumPaymentRequest {

    private Long policyId;
}
