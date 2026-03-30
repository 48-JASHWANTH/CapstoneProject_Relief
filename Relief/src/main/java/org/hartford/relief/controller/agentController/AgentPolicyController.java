package org.hartford.relief.controller.agentController;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.hartford.relief.dto.request.AgentPremiumAdjustRequest;
import org.hartford.relief.dto.response.AgentDashboardResponse;
import org.hartford.relief.dto.response.PolicyResponse;
import org.hartford.relief.service.agentService.AgentPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import org.hartford.relief.service.agentService.AiUnderwritingService;
import org.hartford.relief.dto.response.AiPremiumDecision;

@RestController
@RequestMapping("/api/agents/{agentId}")
@RequiredArgsConstructor
public class AgentPolicyController {

    private final AgentPolicyService agentPolicyService;
    private final AiUnderwritingService aiUnderwritingService;

    // GET /api/agents/{agentId}/policies/{policyId}/ai-premium
    @GetMapping("/policies/{policyId}/ai-premium")
    public ResponseEntity<AiPremiumDecision> getAiPremium(@PathVariable Long agentId,
                                                         @PathVariable Long policyId) {
        return ResponseEntity.ok(aiUnderwritingService.calculatePremiumWithAi(agentId, policyId));
    }

    // GET /api/agents/{agentId}/policies
    @GetMapping("/policies")
    public ResponseEntity<List<PolicyResponse>> getMyPolicies(@PathVariable Long agentId) {
        return ResponseEntity.ok(agentPolicyService.getMyPolicies(agentId));
    }

    // GET /api/agents/{agentId}/policies/{policyId}
    @GetMapping("/policies/{policyId}")
    public ResponseEntity<PolicyResponse> getMyPolicyById(@PathVariable Long agentId,
                                                           @PathVariable Long policyId) {
        return ResponseEntity.ok(agentPolicyService.getMyPolicyById(agentId, policyId));
    }

    // GET /api/agents/{agentId}/policies/by-status?status=PENDING
    @GetMapping("/policies/by-status")
    public ResponseEntity<List<PolicyResponse>> getMyPoliciesByStatus(@PathVariable Long agentId,
                                                                        @RequestParam String status) {
        return ResponseEntity.ok(agentPolicyService.getMyPoliciesByStatus(agentId, status));
    }

    // PATCH /api/agents/{agentId}/policies/{policyId}/adjust-premium
    @PatchMapping("/policies/{policyId}/adjust-premium")
    public ResponseEntity<PolicyResponse> adjustPremium(@PathVariable Long agentId,
                                                         @PathVariable Long policyId,
                                                         @RequestBody AgentPremiumAdjustRequest request) {
        return ResponseEntity.ok(agentPolicyService.adjustPremium(agentId, policyId, request));
    }

    // GET /api/agents/{agentId}/policies/{policyId}/calculate-premium?sumInsured=100000
    @GetMapping("/policies/{policyId}/calculate-premium")
    public ResponseEntity<Double> calculatePremium(@PathVariable Long agentId,
                                                    @PathVariable Long policyId,
                                                    @RequestParam Double sumInsured) {
        return ResponseEntity.ok(agentPolicyService.calculatePremium(agentId, policyId, sumInsured));
    }

    // GET /api/agents/{agentId}/dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<AgentDashboardResponse> getMyDashboard(@PathVariable Long agentId) {
        return ResponseEntity.ok(agentPolicyService.getMyDashboard(agentId));
    }

    // PUT /api/agents/{agentId}/documents/{documentId}/review
    @PutMapping("/documents/{documentId}/review")
    public ResponseEntity<org.hartford.relief.dto.response.PolicyDocumentResponse> reviewDocument(
            @PathVariable Long agentId,
            @PathVariable Long documentId,
            @RequestParam String status,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(agentPolicyService.reviewDocument(agentId, documentId, status, remarks));
    }
}
