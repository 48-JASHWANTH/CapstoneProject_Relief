package org.hartford.relief.controller.userController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.UserPolicyRequest;
import org.hartford.relief.dto.response.PolicyResponse;
import org.hartford.relief.service.userService.UserPolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/policies")
@RequiredArgsConstructor
public class UserPolicyController {

    private final UserPolicyService userPolicyService;

    // POST /api/users/{userId}/policies
    @PostMapping
    public ResponseEntity<PolicyResponse> submitPolicy(@PathVariable Long userId,
                                                        @RequestBody UserPolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userPolicyService.submitPolicy(userId, request));
    }

    // GET /api/users/{userId}/policies
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> getMyPolicies(@PathVariable Long userId) {
        return ResponseEntity.ok(userPolicyService.getMyPolicies(userId));
    }

    // GET /api/users/{userId}/policies/{policyId}
    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyResponse> getMyPolicyById(@PathVariable Long userId,
                                                           @PathVariable Long policyId) {
        return ResponseEntity.ok(userPolicyService.getMyPolicyById(userId, policyId));
    }

    // GET /api/users/{userId}/policies/by-status?status=ACTIVE
    @GetMapping("/by-status")
    public ResponseEntity<List<PolicyResponse>> getMyPoliciesByStatus(@PathVariable Long userId,
                                                                        @RequestParam String status) {
        return ResponseEntity.ok(userPolicyService.getMyPoliciesByStatus(userId, status));
    }

    // POST /api/users/{userId}/policies/{policyId}/documents
    @PostMapping("/{policyId}/documents")
    public ResponseEntity<org.hartford.relief.dto.response.PolicyDocumentResponse> uploadDocument(
            @PathVariable Long userId,
            @PathVariable Long policyId,
            @RequestParam String documentType,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        return ResponseEntity.ok(userPolicyService.uploadDocument(userId, policyId, documentType, file));
    }

    // PUT /api/users/{userId}/policies/{policyId}/advanced-details
    @PutMapping("/{policyId}/advanced-details")
    public ResponseEntity<PolicyResponse> submitAdvancedDetails(
            @PathVariable Long userId,
            @PathVariable Long policyId,
            @RequestBody org.hartford.relief.dto.request.PolicyAdvancedDetailsRequest request) {
        return ResponseEntity.ok(userPolicyService.submitAdvancedDetails(userId, policyId, request));
    }
}
