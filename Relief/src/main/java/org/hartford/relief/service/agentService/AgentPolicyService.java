package org.hartford.relief.service.agentService;

import org.hartford.relief.dto.request.AgentPremiumAdjustRequest;
import org.hartford.relief.dto.response.AgentDashboardResponse;
import org.hartford.relief.dto.response.PolicyResponse;

import java.util.List;

public interface AgentPolicyService {

    List<PolicyResponse> getMyPolicies(Long agentId);

    PolicyResponse getMyPolicyById(Long agentId, Long policyId);

    List<PolicyResponse> getMyPoliciesByStatus(Long agentId, String status);

    PolicyResponse adjustPremium(Long agentId, Long policyId, AgentPremiumAdjustRequest request);

    AgentDashboardResponse getMyDashboard(Long agentId);

    Double calculatePremium(Long agentId, Long policyId, Double sumInsured);
}
