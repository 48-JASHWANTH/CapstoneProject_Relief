package org.hartford.relief.service.impl.adminServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AssignOfficerRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.entity.Claim;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.entity.User;
import org.hartford.relief.exception.InvalidRoleException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.ClaimRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.adminService.AdminClaimService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminClaimServiceImpl implements AdminClaimService {

    private final ClaimRepository claimRepository;
    private final UserRepository  userRepository;

    @Override
    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll()
                .stream()
                .map(this::mapClaim)
                .collect(Collectors.toList());
    }

    @Override
    public ClaimResponse getClaimById(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", claimId));
        return mapClaim(claim);
    }

    @Override
    @Transactional
    public ClaimResponse assignOfficerToClaim(Long claimId, AssignOfficerRequest request) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", claimId));

        User officer = userRepository.findById(request.getOfficerUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getOfficerUserId()));

        boolean isOfficer = officer.getRole() != null &&
                officer.getRole().getName().equalsIgnoreCase("CLAIMS_OFFICER");
        if (!isOfficer) {
            throw new InvalidRoleException(request.getOfficerUserId(), "CLAIMS_OFFICER");
        }

        claim.setAssignedOfficer(officer);
        return mapClaim(claimRepository.save(claim));
    }

    @Override
    public List<ClaimResponse> getUnassignedClaims() {
        return claimRepository.findByAssignedOfficerIsNull()
                .stream()
                .map(this::mapClaim)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimResponse> getClaimsByStatus(String status) {
        return claimRepository.findByStatus(status.toUpperCase())
                .stream()
                .map(this::mapClaim)
                .collect(Collectors.toList());
    }

    private ClaimResponse mapClaim(Claim claim) {
        Policy policy = claim.getPolicy();
        return ClaimResponse.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .policyId(policy != null ? policy.getId() : null)
                .policyNumber(policy != null ? policy.getPolicyNumber() : null)
                .disasterType(policy != null ? policy.getDisasterType() : null)
                .description(claim.getDescription())
                .estimatedLoss(claim.getEstimatedLoss())
                .approvedAmount(claim.getApprovedAmount())
                .status(claim.getStatus())
                .officerRemarks(claim.getOfficerRemarks())
                .filedDate(claim.getFiledDate())
                .resolvedDate(claim.getResolvedDate())
                .assignedOfficerId(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getId() : null)
                .assignedOfficerName(claim.getAssignedOfficer() != null ? claim.getAssignedOfficer().getName() : null)
                .propertyAddress(policy != null ? policy.getPropertyAddress() : null)
                .sumInsured(policy != null ? policy.getSumInsured() : null)
                .premiumAmount(policy != null ? policy.getPremiumAmount() : null)
                .userName(policy != null && policy.getUser() != null ? policy.getUser().getName() : null)
                .region(policy != null ? policy.getRegion() : null)
                .build();
    }
}
