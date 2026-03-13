package org.hartford.relief.controller.claimsOfficerController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.ClaimDecisionRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.dto.response.ClaimsOfficerDashboardResponse;
import org.hartford.relief.service.claimsOfficerService.ClaimsOfficerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims-officer")
@RequiredArgsConstructor
public class ClaimsOfficerController {

    private final ClaimsOfficerService claimsOfficerService;

    // GET /api/claims-officer/{officerId}/dashboard
    @GetMapping("/{officerId}/dashboard")
    public ResponseEntity<ClaimsOfficerDashboardResponse> getDashboard(@PathVariable Long officerId) {
        return ResponseEntity.ok(claimsOfficerService.getDashboard(officerId));
    }

    // GET /api/claims-officer/{officerId}/claims
    @GetMapping("/{officerId}/claims")
    public ResponseEntity<List<ClaimResponse>> getAllClaims(@PathVariable Long officerId) {
        return ResponseEntity.ok(claimsOfficerService.getAllClaims(officerId));
    }

    // GET /api/claims-officer/{officerId}/claims/{claimId}
    @GetMapping("/{officerId}/claims/{claimId}")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable Long officerId,
                                                       @PathVariable Long claimId) {
        return ResponseEntity.ok(claimsOfficerService.getClaimById(claimId));
    }

    // GET /api/claims-officer/{officerId}/claims/by-status?status=FILED
    @GetMapping("/{officerId}/claims/by-status")
    public ResponseEntity<List<ClaimResponse>> getClaimsByStatus(@PathVariable Long officerId,
                                                                   @RequestParam String status) {
        return ResponseEntity.ok(claimsOfficerService.getClaimsByStatus(officerId, status));
    }

    // GET /api/claims-officer/{officerId}/claims/by-disaster-type?disasterType=FLOOD
    @GetMapping("/{officerId}/claims/by-disaster-type")
    public ResponseEntity<List<ClaimResponse>> getClaimsByDisasterType(@PathVariable Long officerId,
                                                                        @RequestParam String disasterType) {
        return ResponseEntity.ok(claimsOfficerService.getClaimsByDisasterType(officerId, disasterType));
    }

    // PATCH /api/claims-officer/{officerId}/claims/{claimId}/under-review
    @PatchMapping("/{officerId}/claims/{claimId}/under-review")
    public ResponseEntity<ClaimResponse> markUnderReview(@PathVariable Long officerId,
                                                          @PathVariable Long claimId) {
        return ResponseEntity.ok(claimsOfficerService.markUnderReview(claimId));
    }

    // PATCH /api/claims-officer/{officerId}/claims/{claimId}/decision
    @PatchMapping("/{officerId}/claims/{claimId}/decision")
    public ResponseEntity<ClaimResponse> decideOnClaim(@PathVariable Long officerId,
                                                        @PathVariable Long claimId,
                                                        @RequestBody ClaimDecisionRequest request) {
        return ResponseEntity.ok(claimsOfficerService.decideOnClaim(claimId, request));
    }
}
