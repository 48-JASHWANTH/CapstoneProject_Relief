package org.hartford.relief.service.impl.userServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.PremiumPaymentRequest;
import org.hartford.relief.dto.response.PaymentResponse;
import org.hartford.relief.entity.Payment;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.entity.RiskPool;
import org.hartford.relief.exception.InvalidStatusTransitionException;
import org.hartford.relief.exception.PremiumAlreadyPaidException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.exception.UnauthorizedAccessException;
import org.hartford.relief.repository.PaymentRepository;
import org.hartford.relief.repository.PolicyRepository;
import org.hartford.relief.repository.RiskPoolRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.userService.UserPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPaymentServiceImpl implements UserPaymentService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final PaymentRepository paymentRepository;
    private final RiskPoolRepository riskPoolRepository;

    @Override
    @Transactional
    public PaymentResponse payPremium(Long userId, PremiumPaymentRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy", request.getPolicyId()));

        // Ownership check
        if (!policy.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Policy", userId);
        }

        // Only APPROVED policies can receive premium payment
        if (!"APPROVED".equalsIgnoreCase(policy.getStatus())) {
            throw new InvalidStatusTransitionException(
                    "Premium can only be paid for APPROVED policies. Current status: " + policy.getStatus());
        }

        // Check if premium was already paid (prevent duplicate)
        boolean alreadyPaid = !paymentRepository
                .findByPolicyIdAndPaymentType(policy.getId(), "PREMIUM")
                .isEmpty();
        if (alreadyPaid) {
            throw new PremiumAlreadyPaidException(policy.getId());
        }

        // Record the premium payment
        Payment payment = Payment.builder()
                .policy(policy)
                .paymentType("PREMIUM")
                .amount(policy.getPremiumAmount())
                .paymentStatus("COMPLETED")
                .paymentDate(LocalDateTime.now())
                .riskPool(policy.getRiskPool())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Activate the policy after premium payment
        policy.setStatus("ACTIVE");
        policyRepository.save(policy);

        // Update the RiskPool: add premium to totalPremiumCollected
        if (policy.getRiskPool() != null) {
            RiskPool pool = policy.getRiskPool();
            double current = pool.getTotalPremiumCollected() != null ? pool.getTotalPremiumCollected() : 0.0;
            pool.setTotalPremiumCollected(current + policy.getPremiumAmount());
            riskPoolRepository.save(pool);
        }

        return mapToResponse(saved);
    }

    @Override
    public List<PaymentResponse> getMyPayments(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Fetch all payments for all policies belonging to this user
        return policyRepository.findByUserId(userId)
                .stream()
                .flatMap(policy -> paymentRepository.findByPolicyId(policy.getId()).stream())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
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
