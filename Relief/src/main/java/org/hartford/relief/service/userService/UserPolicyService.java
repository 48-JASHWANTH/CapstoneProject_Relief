package org.hartford.relief.service.userService;

import org.hartford.relief.dto.request.UserPolicyRequest;
import org.hartford.relief.dto.response.PolicyResponse;

import java.util.List;

public interface UserPolicyService {

    PolicyResponse submitPolicy(Long userId, UserPolicyRequest request);

    List<PolicyResponse> getMyPolicies(Long userId);

    PolicyResponse getMyPolicyById(Long userId, Long policyId);

    List<PolicyResponse> getMyPoliciesByStatus(Long userId, String status);

    org.hartford.relief.dto.response.PolicyDocumentResponse uploadDocument(Long userId, Long policyId, String documentType, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException;

    PolicyResponse submitAdvancedDetails(Long userId, Long policyId, org.hartford.relief.dto.request.PolicyAdvancedDetailsRequest request);
}
