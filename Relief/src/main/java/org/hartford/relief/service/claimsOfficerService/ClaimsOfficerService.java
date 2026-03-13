package org.hartford.relief.service.claimsOfficerService;

import org.hartford.relief.dto.request.ClaimDecisionRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.dto.response.ClaimsOfficerDashboardResponse;

import java.util.List;

public interface ClaimsOfficerService {

    // ── Claim views ────────────────────────────────
    List<ClaimResponse> getAllClaims(Long officerId);

    ClaimResponse getClaimById(Long claimId);

    List<ClaimResponse> getClaimsByStatus(Long officerId, String status);

    List<ClaimResponse> getClaimsByDisasterType(Long officerId, String disasterType);

    // ── Claim lifecycle actions ────────────────────
    // Move FILED → UNDER_REVIEW
    ClaimResponse markUnderReview(Long claimId);

    // Approve or Reject → APPROVED / REJECTED, triggers payout if approved
    ClaimResponse decideOnClaim(Long claimId, ClaimDecisionRequest request);

    // ── Dashboard ─────────────────────────────────
    ClaimsOfficerDashboardResponse getDashboard(Long officerId);
}
