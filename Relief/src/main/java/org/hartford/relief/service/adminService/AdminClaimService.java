package org.hartford.relief.service.adminService;

import org.hartford.relief.dto.request.AssignOfficerRequest;
import org.hartford.relief.dto.response.ClaimResponse;

import java.util.List;

public interface AdminClaimService {

    List<ClaimResponse> getAllClaims();

    ClaimResponse getClaimById(Long claimId);

    ClaimResponse assignOfficerToClaim(Long claimId, AssignOfficerRequest request);

    List<ClaimResponse> getUnassignedClaims();

    List<ClaimResponse> getClaimsByStatus(String status);
}
