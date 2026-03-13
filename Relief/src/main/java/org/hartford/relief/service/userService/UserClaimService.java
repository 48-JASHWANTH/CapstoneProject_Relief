package org.hartford.relief.service.userService;

import org.hartford.relief.dto.request.ClaimRequest;
import org.hartford.relief.dto.response.ClaimResponse;

import java.util.List;

public interface UserClaimService {

    ClaimResponse fileClaim(Long userId, ClaimRequest request);

    List<ClaimResponse> getMyClaims(Long userId);

    ClaimResponse getMyClaimById(Long userId, Long claimId);
}
