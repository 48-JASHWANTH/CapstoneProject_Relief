package org.hartford.relief.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDashboardResponse {

    private Long userId;
    private String name;
    private String email;

    private long totalPolicies;
    private long activePolicies;
    private long pendingPolicies;

    private long totalClaims;
    private long approvedClaims;
    private long pendingClaims;
    private long rejectedClaims;

    private long totalPayments;
    private Double totalPremiumPaid;

    private List<PolicyResponse> policies;
    private List<ClaimResponse> claims;
    private List<PaymentResponse> payments;
}
