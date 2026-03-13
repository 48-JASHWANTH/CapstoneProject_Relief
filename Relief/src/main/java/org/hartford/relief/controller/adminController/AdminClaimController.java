package org.hartford.relief.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AssignOfficerRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.service.adminService.AdminClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/claims")
@RequiredArgsConstructor
public class AdminClaimController {

    private final AdminClaimService adminClaimService;

    // GET /api/admin/claims
    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {
        return ResponseEntity.ok(adminClaimService.getAllClaims());
    }

    // GET /api/admin/claims/unassigned
    @GetMapping("/unassigned")
    public ResponseEntity<List<ClaimResponse>> getUnassignedClaims() {
        return ResponseEntity.ok(adminClaimService.getUnassignedClaims());
    }

    // GET /api/admin/claims/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable Long id) {
        return ResponseEntity.ok(adminClaimService.getClaimById(id));
    }

    // GET /api/admin/claims/by-status?status=FILED
    @GetMapping("/by-status")
    public ResponseEntity<List<ClaimResponse>> getClaimsByStatus(@RequestParam String status) {
        return ResponseEntity.ok(adminClaimService.getClaimsByStatus(status));
    }

    // PATCH /api/admin/claims/{id}/assign-officer
    @PatchMapping("/{id}/assign-officer")
    public ResponseEntity<ClaimResponse> assignOfficer(@PathVariable Long id,
                                                        @RequestBody AssignOfficerRequest request) {
        return ResponseEntity.ok(adminClaimService.assignOfficerToClaim(id, request));
    }
}
