package org.hartford.relief.service.impl.userServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.response.*;
import org.hartford.relief.entity.Claim;
import org.hartford.relief.entity.Payment;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.entity.User;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.ClaimRepository;
import org.hartford.relief.repository.PaymentRepository;
import org.hartford.relief.repository.PolicyRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.userService.UserDashboardService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDashboardServiceImpl implements UserDashboardService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public UserDashboardResponse getMyDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        List<Policy> policies  = policyRepository.findByUserId(userId);
        List<Claim>  claims    = claimRepository.findByPolicy_UserId(userId);

        // Aggregate payments across all user's policies
        List<Payment> payments = policies.stream()
                .flatMap(p -> paymentRepository.findByPolicyId(p.getId()).stream())
                .collect(Collectors.toList());

        long activePolicies   = policies.stream().filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).count();
        long pendingPolicies  = policies.stream().filter(p -> "PENDING".equalsIgnoreCase(p.getStatus())).count();

        long approvedClaims  = claims.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count();
        long pendingClaims   = claims.stream().filter(c -> "FILED".equalsIgnoreCase(c.getStatus())
                || "UNDER_REVIEW".equalsIgnoreCase(c.getStatus())
                || "SURVEY_ASSIGNED".equalsIgnoreCase(c.getStatus())).count();
        long rejectedClaims  = claims.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count();

        double totalPremiumPaid = payments.stream()
                .filter(p -> "PREMIUM".equalsIgnoreCase(p.getPaymentType())
                        && "COMPLETED".equalsIgnoreCase(p.getPaymentStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        return UserDashboardResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .totalPolicies(policies.size())
                .activePolicies(activePolicies)
                .pendingPolicies(pendingPolicies)
                .totalClaims(claims.size())
                .approvedClaims(approvedClaims)
                .pendingClaims(pendingClaims)
                .rejectedClaims(rejectedClaims)
                .totalPayments(payments.size())
                .totalPremiumPaid(Math.round(totalPremiumPaid * 100.0) / 100.0)
                .policies(policies.stream().map(this::mapPolicy).collect(Collectors.toList()))
                .claims(claims.stream().map(this::mapClaim).collect(Collectors.toList()))
                .payments(payments.stream().map(this::mapPayment).collect(Collectors.toList()))
                .build();
    }

    private PolicyResponse mapPolicy(Policy policy) {
        return PolicyResponse.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .userId(policy.getUser() != null ? policy.getUser().getId() : null)
                .userName(policy.getUser() != null ? policy.getUser().getName() : null)
                .agentId(policy.getAgent() != null ? policy.getAgent().getId() : null)
                .agentName(policy.getAgent() != null ? policy.getAgent().getUser().getName() : null)
                .disasterType(policy.getDisasterType())
                .policyType(policy.getPolicyType())
                .propertyAddress(policy.getPropertyAddress())
                .propertyValue(policy.getPropertyValue())
                .sumInsured(policy.getSumInsured())
                .premiumAmount(policy.getPremiumAmount())
                .status(policy.getStatus())
                .remarks(policy.getRemarks())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .disasterZoneName(policy.getDisasterZone() != null ? policy.getDisasterZone().getZoneName() : null)
                .riskPoolDisasterType(policy.getRiskPool() != null ? policy.getRiskPool().getDisasterType() : null)
                .build();
    }

    private ClaimResponse mapClaim(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .policyId(claim.getPolicy() != null ? claim.getPolicy().getId() : null)
                .policyNumber(claim.getPolicy() != null ? claim.getPolicy().getPolicyNumber() : null)
                .description(claim.getDescription())
                .estimatedLoss(claim.getEstimatedLoss())
                .approvedAmount(claim.getApprovedAmount())
                .status(claim.getStatus())
                .officerRemarks(claim.getOfficerRemarks())
                .filedDate(claim.getFiledDate())
                .resolvedDate(claim.getResolvedDate())
                .build();
    }

    private PaymentResponse mapPayment(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .policyId(payment.getPolicy() != null ? payment.getPolicy().getId() : null)
                .policyNumber(payment.getPolicy() != null ? payment.getPolicy().getPolicyNumber() : null)
                .claimId(payment.getClaim() != null ? payment.getClaim().getId() : null)
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
