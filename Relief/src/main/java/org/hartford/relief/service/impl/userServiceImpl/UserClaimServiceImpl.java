package org.hartford.relief.service.impl.userServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.ClaimRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.entity.Claim;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.exception.BadRequestException;
import org.hartford.relief.exception.ClaimAlreadyPendingException;
import org.hartford.relief.exception.InvalidAmountException;
import org.hartford.relief.exception.InvalidStatusTransitionException;
import org.hartford.relief.exception.PolicyCoverageExpiredException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.exception.UnauthorizedAccessException;
import org.hartford.relief.repository.ClaimRepository;
import org.hartford.relief.repository.PolicyRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.userService.UserClaimService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserClaimServiceImpl implements UserClaimService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    @Override
    @Transactional
    public ClaimResponse fileClaim(Long userId, ClaimRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy", request.getPolicyId()));

        // Ownership check
        if (!policy.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Policy", userId);
        }

        // Policy must be ACTIVE
        String policyStatus = policy.getStatus();
        if (!"ACTIVE".equalsIgnoreCase(policyStatus)) {
            throw new InvalidStatusTransitionException(
                    "Claims can only be filed on ACTIVE policies. Current status: " + policyStatus);
        }

        // Policy must be within coverage period
        LocalDate today = LocalDate.now();
        if (today.isBefore(policy.getStartDate()) || today.isAfter(policy.getEndDate())) {
            throw new PolicyCoverageExpiredException(policy.getId());
        }

        // No pending (FILED or UNDER_REVIEW) claim already exists for this policy
        boolean hasPendingClaim = claimRepository.findByPolicyId(policy.getId())
                .stream()
                .anyMatch(c -> "FILED".equalsIgnoreCase(c.getStatus())
                        || "UNDER_REVIEW".equalsIgnoreCase(c.getStatus()));
        if (hasPendingClaim) {
            throw new ClaimAlreadyPendingException(policy.getId());
        }

        // Description must be meaningful
        if (request.getDescription() == null || request.getDescription().trim().length() < 20) {
            throw new BadRequestException("Description must be at least 20 characters.");
        }

        // Estimated loss must not exceed sum insured
        if (request.getEstimatedLoss() == null || request.getEstimatedLoss() <= 0) {
            throw new InvalidAmountException("Estimated loss must be greater than zero.");
        }
        if (request.getEstimatedLoss() > policy.getSumInsured()) {
            throw new InvalidAmountException(
                    "Estimated loss", request.getEstimatedLoss(), policy.getSumInsured());
        }

        String claimNumber = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Claim claim = Claim.builder()
                .claimNumber(claimNumber)
                .policy(policy)
                .riskPool(policy.getRiskPool())
                .description(request.getDescription())
                .estimatedLoss(request.getEstimatedLoss())
                .status("FILED")
                .filedDate(LocalDateTime.now())
                .build();

        return mapToResponse(claimRepository.save(claim));
    }

    @Override
    public List<ClaimResponse> getMyClaims(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return claimRepository.findByPolicy_UserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClaimResponse getMyClaimById(Long userId, Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", claimId));
        if (!claim.getPolicy().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Claim", userId);
        }
        return mapToResponse(claim);
    }

    private ClaimResponse mapToResponse(Claim claim) {
        Policy policy = claim.getPolicy();
        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .policyId(policy != null ? policy.getId() : null)
                .policyNumber(policy != null ? policy.getPolicyNumber() : null)
                .disasterType(policy != null ? policy.getDisasterType() : null)
                .propertyAddress(policy != null ? policy.getPropertyAddress() : null)
                .sumInsured(policy != null ? policy.getSumInsured() : null)
                .premiumAmount(policy != null ? policy.getPremiumAmount() : null)
                .description(claim.getDescription())
                .estimatedLoss(claim.getEstimatedLoss())
                .approvedAmount(claim.getApprovedAmount())
                .status(claim.getStatus())
                .officerRemarks(claim.getOfficerRemarks())
                .filedDate(claim.getFiledDate())
                .resolvedDate(claim.getResolvedDate())
                .build();
    }
}
