package org.hartford.relief.service.impl.claimsOfficerServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.ClaimDecisionRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.dto.response.ClaimsOfficerDashboardResponse;
import org.hartford.relief.dto.response.RiskPoolResponse;
import org.hartford.relief.entity.Claim;
import org.hartford.relief.entity.Payment;
import org.hartford.relief.entity.RiskPool;
import org.hartford.relief.exception.BadRequestException;
import org.hartford.relief.exception.InvalidAmountException;
import org.hartford.relief.exception.InvalidStatusTransitionException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.ClaimRepository;
import org.hartford.relief.repository.PaymentRepository;
import org.hartford.relief.repository.RiskPoolRepository;
import org.hartford.relief.service.claimsOfficerService.ClaimsOfficerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimsOfficerServiceImpl implements ClaimsOfficerService {

    private final ClaimRepository       claimRepository;
    private final PaymentRepository     paymentRepository;
    private final RiskPoolRepository    riskPoolRepository;

    // ══════════════════════════════════════════════
    // Claim views
    // ══════════════════════════════════════════════

    @Override
    public List<ClaimResponse> getAllClaims(Long officerId) {
        return claimRepository.findByAssignedOfficer_Id(officerId)
                .stream()
                .map(this::mapClaim)
                .collect(Collectors.toList());
    }

    @Override
    public ClaimResponse getClaimById(Long claimId) {
        return mapClaim(findClaim(claimId));
    }

    @Override
    public List<ClaimResponse> getClaimsByStatus(Long officerId, String status) {
        return claimRepository.findByAssignedOfficer_Id(officerId)
                .stream()
                .filter(c -> c.getStatus().equalsIgnoreCase(status))
                .map(this::mapClaim)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimResponse> getClaimsByDisasterType(Long officerId, String disasterType) {
        return claimRepository.findByAssignedOfficer_Id(officerId)
                .stream()
                .filter(c -> c.getPolicy() != null
                        && disasterType.equalsIgnoreCase(c.getPolicy().getDisasterType()))
                .map(this::mapClaim)
                .collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════
    // Claim lifecycle actions
    // ══════════════════════════════════════════════

    @Override
    @Transactional
    public ClaimResponse markUnderReview(Long claimId) {
        Claim claim = findClaim(claimId);
        if (!"FILED".equalsIgnoreCase(claim.getStatus())) {
            throw new InvalidStatusTransitionException(
                    "Only FILED claims can be moved to UNDER_REVIEW. Current status: " + claim.getStatus());
        }
        claim.setStatus("UNDER_REVIEW");
        return mapClaim(claimRepository.save(claim));
    }

    @Override
    @Transactional
    public ClaimResponse decideOnClaim(Long claimId, ClaimDecisionRequest request) {
        Claim claim = findClaim(claimId);

        // Officer can decide on FILED or UNDER_REVIEW claims
        if ("APPROVED".equalsIgnoreCase(claim.getStatus())
                || "REJECTED".equalsIgnoreCase(claim.getStatus())
                || "PAID".equalsIgnoreCase(claim.getStatus())) {
            throw new InvalidStatusTransitionException(
                    "Claim has already been decided. Current status: " + claim.getStatus());
        }

        String decision = request.getDecision() == null ? "" : request.getDecision().toUpperCase();
        if (!decision.equals("APPROVED") && !decision.equals("REJECTED")) {
            throw new BadRequestException("Decision must be APPROVED or REJECTED.");
        }

        // Store officer remarks
        if (request.getRemarks() != null && !request.getRemarks().isBlank()) {
            claim.setOfficerRemarks(request.getRemarks());
        }

        if (decision.equals("REJECTED")) {
            claim.setStatus("REJECTED");
            claim.setResolvedDate(LocalDateTime.now());
            return mapClaim(claimRepository.save(claim));
        }

        // ── APPROVED path ──────────────────────────────────────────────────────
        if (request.getApprovedAmount() == null || request.getApprovedAmount() <= 0) {
            throw new InvalidAmountException("Approved amount must be greater than zero.");
        }
        if (request.getApprovedAmount() > claim.getPolicy().getSumInsured()) {
            throw new InvalidAmountException(
                    "Approved amount", request.getApprovedAmount(), claim.getPolicy().getSumInsured());
        }

        claim.setApprovedAmount(request.getApprovedAmount());
        claim.setStatus("APPROVED");
        claim.setResolvedDate(LocalDateTime.now());
        claimRepository.save(claim);

        // Trigger payout payment
        Payment payout = Payment.builder()
                .policy(claim.getPolicy())
                .claim(claim)
                .riskPool(claim.getRiskPool())
                .paymentType("CLAIM_PAYOUT")
                .amount(request.getApprovedAmount())
                .paymentStatus("COMPLETED")
                .paymentDate(LocalDateTime.now())
                .build();
        paymentRepository.save(payout);

        // Update claim status to PAID after payout
        claim.setStatus("PAID");
        Claim saved = claimRepository.save(claim);

        // Update RiskPool: add to totalClaimsPaid and re-evaluate threshold
        if (claim.getRiskPool() != null) {
            RiskPool pool = claim.getRiskPool();
            double current = pool.getTotalClaimsPaid() != null ? pool.getTotalClaimsPaid() : 0.0;
            pool.setTotalClaimsPaid(current + request.getApprovedAmount());

            // Auto-evaluate threshold
            if (pool.getTotalPremiumCollected() != null && pool.getTotalPremiumCollected() > 0
                    && pool.getThresholdPercentage() != null) {
                double ratio = (pool.getTotalClaimsPaid() / pool.getTotalPremiumCollected()) * 100;
                pool.setPoolStatus(ratio >= pool.getThresholdPercentage() ? "CRITICAL" : "ACTIVE");
            }
            riskPoolRepository.save(pool);
        }

        return mapClaim(saved);
    }

    // ══════════════════════════════════════════════
    // Dashboard
    // ══════════════════════════════════════════════

    @Override
    public ClaimsOfficerDashboardResponse getDashboard(Long officerId) {
        List<Claim> all = claimRepository.findByAssignedOfficer_Id(officerId);

        long filed           = count(all, "FILED");
        long underReview     = count(all, "UNDER_REVIEW");
        long approved        = count(all, "APPROVED");
        long rejected        = count(all, "REJECTED");
        long paid            = count(all, "PAID");

        double totalApproved = all.stream()
                .filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())
                        || "PAID".equalsIgnoreCase(c.getStatus()))
                .mapToDouble(c -> c.getApprovedAmount() != null ? c.getApprovedAmount() : 0.0)
                .sum();

        double totalPaid = paymentRepository.findByPaymentType("CLAIM_PAYOUT")
                .stream()
                .filter(p -> "COMPLETED".equalsIgnoreCase(p.getPaymentStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        // Claims grouped by disaster type
        Map<String, Long> byDisasterType = all.stream()
                .filter(c -> c.getPolicy() != null && c.getPolicy().getDisasterType() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getPolicy().getDisasterType().toUpperCase(),
                        Collectors.counting()));

        // Claims grouped by status
        Map<String, Long> byStatus = all.stream()
                .filter(c -> c.getStatus() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getStatus().toUpperCase(),
                        Collectors.counting()));

        // Risk pool snapshot
        List<RiskPoolResponse> poolSnapshot = riskPoolRepository.findAll()
                .stream()
                .map(this::mapRiskPool)
                .collect(Collectors.toList());

        // Claims needing attention: FILED (latest 10)
        List<ClaimResponse> pendingAttention = all.stream()
                .filter(c -> "FILED".equalsIgnoreCase(c.getStatus()))
                .sorted((a, b) -> Long.compare(b.getId(), a.getId()))
                .limit(10)
                .map(this::mapClaim)
                .collect(Collectors.toList());

        return ClaimsOfficerDashboardResponse.builder()
                .totalClaims(all.size())
                .filedClaims(filed)
                .underReviewClaims(underReview)
                .approvedClaims(approved)
                .rejectedClaims(rejected)
                .paidClaims(paid)
                .totalApprovedAmount(Math.round(totalApproved * 100.0) / 100.0)
                .totalPaidAmount(Math.round(totalPaid * 100.0) / 100.0)
                .claimsByDisasterType(byDisasterType)
                .claimsByStatus(byStatus)
                .riskPoolSnapshot(poolSnapshot)
                .pendingAttentionClaims(pendingAttention)
                .build();
    }

    // ══════════════════════════════════════════════
    // Private helpers
    // ══════════════════════════════════════════════

    private Claim findClaim(Long claimId) {
        return claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", claimId));
    }

    private long count(List<Claim> claims, String status) {
        return claims.stream().filter(c -> status.equalsIgnoreCase(c.getStatus())).count();
    }

    private ClaimResponse mapClaim(Claim claim) {
        org.hartford.relief.entity.Policy policy = claim.getPolicy();
        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .policyId(policy != null ? policy.getId() : null)
                .policyNumber(policy != null ? policy.getPolicyNumber() : null)
                .disasterType(policy != null ? policy.getDisasterType() : null)
                .description(claim.getDescription())
                .estimatedLoss(claim.getEstimatedLoss())
                .incidentDate(claim.getIncidentDate())
                .damageType(claim.getDamageType())
                .approvedAmount(claim.getApprovedAmount())
                .status(claim.getStatus())
                .officerRemarks(claim.getOfficerRemarks())
                .filedDate(claim.getFiledDate())
                .resolvedDate(claim.getResolvedDate())
                .documents(claim.getDocuments() == null ? java.util.Collections.emptyList() : claim.getDocuments().stream()
                        .map(d -> org.hartford.relief.dto.response.ClaimDocumentResponse.builder()
                                .id(d.getId())
                                .claimId(claim.getId())
                                .documentType(d.getDocumentType())
                                .fileUrl(d.getFileUrl())
                                .documentStatus(d.getDocumentStatus())
                                .officerRemarks(d.getOfficerRemarks())
                                .uploadedAt(d.getUploadedAt())
                                .build())
                        .collect(Collectors.toList()))
                .assignedOfficerId(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getId() : null)
                .assignedOfficerName(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getName() : null)
                .propertyAddress(policy != null ? policy.getPropertyAddress() : null)
                .sumInsured(policy != null ? policy.getSumInsured() : null)
                .premiumAmount(policy != null ? policy.getPremiumAmount() : null)
                .userName(policy != null && policy.getUser() != null ? policy.getUser().getName() : null)
                .build();
    }

    private RiskPoolResponse mapRiskPool(RiskPool pool) {
        double ratio = (pool.getTotalPremiumCollected() != null && pool.getTotalPremiumCollected() > 0
                && pool.getTotalClaimsPaid() != null)
                ? (pool.getTotalClaimsPaid() / pool.getTotalPremiumCollected()) * 100 : 0.0;
        boolean critical = pool.getThresholdPercentage() != null && ratio >= pool.getThresholdPercentage();

        return RiskPoolResponse.builder()
                .id(pool.getId())
                .disasterType(pool.getDisasterType())
                .totalPremiumCollected(pool.getTotalPremiumCollected())
                .totalClaimsPaid(pool.getTotalClaimsPaid())
                .thresholdPercentage(pool.getThresholdPercentage())
                .poolStatus(pool.getPoolStatus())
                .criticalFlag(critical)
                .totalPolicies(pool.getPolicies() != null ? pool.getPolicies().size() : 0)
                .totalClaims(pool.getClaims() != null ? pool.getClaims().size() : 0)
                .build();
    }
}
