package org.hartford.relief.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long policyId;
    private String policyNumber;
    private Long claimId;
    private String paymentType;
    private Double amount;
    private String paymentStatus;
    private LocalDateTime paymentDate;
}
