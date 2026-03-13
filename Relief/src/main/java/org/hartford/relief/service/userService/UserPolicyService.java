package org.hartford.relief.service.userService;

import org.hartford.relief.dto.request.UserPolicyRequest;
import org.hartford.relief.dto.response.PolicyResponse;

import java.util.List;

public interface UserPolicyService {

    PolicyResponse submitPolicy(Long userId, UserPolicyRequest request);

    List<PolicyResponse> getMyPolicies(Long userId);

    PolicyResponse getMyPolicyById(Long userId, Long policyId);

    List<PolicyResponse> getMyPoliciesByStatus(Long userId, String status);
}
