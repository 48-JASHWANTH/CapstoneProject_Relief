package org.hartford.relief.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AssignAgentRequest;
import org.hartford.relief.dto.request.PolicyApprovalRequest;
import org.hartford.relief.dto.response.PolicyResponse;
import org.hartford.relief.service.adminService.AdminPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/policies")
@RequiredArgsConstructor
public class AdminPolicyController {

    private final AdminPolicyService adminPolicyService;

    // GET /api/admin/policies
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(adminPolicyService.getAllPolicies());
    }

    // GET /api/admin/policies/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(adminPolicyService.getPolicyById(id));
    }

    // PATCH /api/admin/policies/{id}/approval
    @PatchMapping("/{id}/approval")
    public ResponseEntity<PolicyResponse> approveOrRejectPolicy(@PathVariable Long id,
                                                                  @RequestBody PolicyApprovalRequest request) {
        return ResponseEntity.ok(adminPolicyService.approveOrRejectPolicy(id, request));
    }

    // PATCH /api/admin/policies/{id}/assign-agent
    @PatchMapping("/{id}/assign-agent")
    public ResponseEntity<PolicyResponse> assignAgent(@PathVariable Long id,
                                                       @RequestBody AssignAgentRequest request) {
        return ResponseEntity.ok(adminPolicyService.assignAgentToPolicy(id, request));
    }

    // GET /api/admin/policies/by-status?status=PENDING
    @GetMapping("/by-status")
    public ResponseEntity<List<PolicyResponse>> getPoliciesByStatus(@RequestParam String status) {
        return ResponseEntity.ok(adminPolicyService.getPoliciesByStatus(status));
    }

    // GET /api/admin/policies/by-disaster-type?disasterType=FLOOD
    @GetMapping("/by-disaster-type")
    public ResponseEntity<List<PolicyResponse>> getPoliciesByDisasterType(@RequestParam String disasterType) {
        return ResponseEntity.ok(adminPolicyService.getPoliciesByDisasterType(disasterType));
    }
}
