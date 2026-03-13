package org.hartford.relief.service.adminService;

import org.hartford.relief.dto.request.AssignAgentRequest;
import org.hartford.relief.dto.request.PolicyApprovalRequest;
import org.hartford.relief.dto.response.PolicyResponse;

import java.util.List;

public interface AdminPolicyService {

    List<PolicyResponse> getAllPolicies();

    PolicyResponse getPolicyById(Long id);

    PolicyResponse approveOrRejectPolicy(Long id, PolicyApprovalRequest request);

    PolicyResponse assignAgentToPolicy(Long policyId, AssignAgentRequest request);

    List<PolicyResponse> getPoliciesByStatus(String status);

    List<PolicyResponse> getPoliciesByDisasterType(String disasterType);
}
