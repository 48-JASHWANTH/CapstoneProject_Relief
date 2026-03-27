package org.hartford.relief.service.impl.userServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.UserPolicyRequest;
import org.hartford.relief.dto.response.PolicyResponse;
import org.hartford.relief.entity.DisasterZone;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.entity.RiskPool;
import org.hartford.relief.entity.User;
import org.hartford.relief.exception.BadRequestException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.DisasterZoneRepository;
import org.hartford.relief.repository.PolicyRepository;
import org.hartford.relief.repository.RiskPoolRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.userService.UserPolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hartford.relief.dto.response.PolicyDocumentResponse;
import org.hartford.relief.dto.request.PolicyAdvancedDetailsRequest;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserPolicyServiceImpl implements UserPolicyService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final DisasterZoneRepository disasterZoneRepository;
    private final RiskPoolRepository riskPoolRepository;
    private final org.hartford.relief.service.FileStorageService fileStorageService;
    private final org.hartford.relief.repository.PolicyDocumentRepository policyDocumentRepository;

    /**
     * Premium = sumInsured * baseRate * (riskFactor / 10.0)
     *   Base rate: BASIC=0.3%, STANDARD=0.5%, PREMIUM=0.8%
     *   riskFactor is the zone's computed risk score (4.5 – 8.0 scale)
     *   riskFactor/10 normalises to a multiplier around 0.45x – 0.8x
     */
    private double calculatePremium(String policyType, double riskFactor, Double sumInsured) {
        double baseRate = switch (policyType.toUpperCase()) {
            case "STANDARD" -> 0.005;
            case "PREMIUM"  -> 0.008;
            default          -> 0.003;  // BASIC
        };
        double multiplier = riskFactor / 10.0;
        return Math.round(sumInsured * baseRate * multiplier * 100.0) / 100.0;
    }

    @Override
    @Transactional
    public PolicyResponse submitPolicy(Long userId, UserPolicyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (request.getDisasterType() == null || request.getDisasterType().isBlank())
            throw new BadRequestException("Disaster type is required.");
        if (request.getRegion() == null || request.getRegion().isBlank())
            throw new BadRequestException("Region is required.");
        if (request.getTenure() == null || request.getTenure() <= 0)
            throw new BadRequestException("Tenure must be at least 1 year.");
        if (request.getSumInsured() == null || request.getSumInsured() <= 0)
            throw new BadRequestException("Sum insured must be greater than zero.");

        String disasterType = request.getDisasterType().toUpperCase();
        String region       = request.getRegion().toUpperCase();

        // Find disaster zone matching disaster type + region
        DisasterZone zone = disasterZoneRepository
                .findByDisasterTypeAndLocation(disasterType, region)
                .orElse(null);

        // Risk factor: use zone's value when available, else fall back to type-based default
        double riskFactor = (zone != null && zone.getRiskFactor() != null)
                ? zone.getRiskFactor()
                : switch (disasterType) {
                    case "FLOOD"      -> 5.0;
                    case "EARTHQUAKE" -> 6.5;
                    case "CYCLONE"    -> 6.0;
                    case "WILDFIRE"   -> 4.5;
                    default            -> 5.0;
                };

        RiskPool riskPool = riskPoolRepository.findByDisasterType(disasterType).orElse(null);

        String policyType   = (request.getPolicyType() != null && !request.getPolicyType().isBlank())
                ? request.getPolicyType() : "BASIC";
        // Use the customer-entered premium amount directly; agent will set the correct calculated premium later
        double premium      = (request.getPremiumAmount() != null && request.getPremiumAmount() > 0)
                ? request.getPremiumAmount()
                : calculatePremium(policyType, riskFactor, request.getSumInsured());
        String policyNumber = "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate   = startDate.plusYears(request.getTenure());

        Policy policy = Policy.builder()
                .policyNumber(policyNumber)
                .user(user).agent(null)
                .disasterZone(zone).riskPool(riskPool)
                .disasterType(disasterType)
                .policyType(policyType.toUpperCase())
                .propertyAddress(request.getPropertyAddress())
                .propertyValue(request.getPropertyValue())
                .sumInsured(request.getSumInsured())
                .premiumAmount(premium)
                .region(region)
                .tenure(request.getTenure())
                .status("PENDING")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return mapToResponse(policyRepository.save(policy));
    }

    @Override
    public List<PolicyResponse> getMyPolicies(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return policyRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PolicyResponse getMyPolicyById(Long userId, Long policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", policyId));
        if (!policy.getUser().getId().equals(userId))
            throw new BadRequestException("Policy does not belong to this user.");
        return mapToResponse(policy);
    }

    @Override
    public List<PolicyResponse> getMyPoliciesByStatus(Long userId, String status) {
        return policyRepository.findByUserIdAndStatus(userId, status.toUpperCase())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PolicyResponse mapToResponse(Policy policy) {
        DisasterZone zone = policy.getDisasterZone();
        java.util.List<org.hartford.relief.dto.response.PolicyDocumentResponse> docs = policy.getDocuments().stream()
                .map(d -> org.hartford.relief.dto.response.PolicyDocumentResponse.builder()
                        .id(d.getId())
                        .policyId(d.getPolicy().getId())
                        .documentType(d.getDocumentType())
                        .fileUrl(d.getFileUrl())
                        .documentStatus(d.getDocumentStatus())
                        .agentRemarks(d.getAgentRemarks())
                        .uploadedAt(d.getUploadedAt())
                        .build())
                .collect(Collectors.toList());

        return PolicyResponse.builder()
                .id(policy.getId())
                .policyNumber(policy.getPolicyNumber())
                .userId(policy.getUser() != null ? policy.getUser().getId() : null)
                .userName(policy.getUser() != null ? policy.getUser().getName() : null)
                .agentId(policy.getAgent() != null ? policy.getAgent().getId() : null)
                .agentName(policy.getAgent() != null ? policy.getAgent().getUser().getName() : null)
                .disasterType(policy.getDisasterType())
                .policyType(policy.getPolicyType())
                .propertyAddress(policy.getPropertyAddress())
                .propertyValue(policy.getPropertyValue())
                .sumInsured(policy.getSumInsured())
                .premiumAmount(policy.getPremiumAmount())
                .status(policy.getStatus())
                .remarks(policy.getRemarks())
                .startDate(policy.getStartDate())
                .endDate(policy.getEndDate())
                .region(policy.getRegion())
                .tenure(policy.getTenure())
                .disasterZoneId(zone != null ? zone.getId() : null)
                .disasterZoneName(zone != null ? zone.getZoneName() : null)
                .disasterZoneRiskFactor(zone != null ? zone.getRiskFactor() : null)
                .riskPoolDisasterType(policy.getRiskPool() != null ? policy.getRiskPool().getDisasterType() : null)
                .yearBuilt(policy.getYearBuilt())
                .roofAge(policy.getRoofAge())
                .constructionMaterial(policy.getConstructionMaterial())
                .previousClaimsHistory(policy.getPreviousClaimsHistory())
                .safetyFeatures(policy.getSafetyFeatures())
                .documents(docs)
                .build();
    }

    @Override
    @Transactional
    public org.hartford.relief.dto.response.PolicyDocumentResponse uploadDocument(Long userId, Long policyId, String documentType, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", policyId));
        if (!policy.getUser().getId().equals(userId))
            throw new BadRequestException("Policy does not belong to this user.");

        String fileName = fileStorageService.storeFile(file, policyId);

        org.hartford.relief.entity.PolicyDocument document = org.hartford.relief.entity.PolicyDocument.builder()
                .policy(policy)
                .documentType(documentType)
                .fileUrl(fileName)
                .documentStatus("PENDING")
                .uploadedAt(java.time.LocalDateTime.now())
                .build();

        document = policyDocumentRepository.save(document);
        policy.getDocuments().add(document);

        return org.hartford.relief.dto.response.PolicyDocumentResponse.builder()
                .id(document.getId())
                .policyId(policyId)
                .documentType(document.getDocumentType())
                .fileUrl(document.getFileUrl())
                .documentStatus(document.getDocumentStatus())
                .uploadedAt(document.getUploadedAt())
                .build();
    }

    @Override
    @Transactional
    public PolicyResponse submitAdvancedDetails(Long userId, Long policyId, org.hartford.relief.dto.request.PolicyAdvancedDetailsRequest request) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", policyId));
        if (!policy.getUser().getId().equals(userId))
            throw new BadRequestException("Policy does not belong to this user.");

        policy.setYearBuilt(request.getYearBuilt());
        policy.setRoofAge(request.getRoofAge());
        policy.setConstructionMaterial(request.getConstructionMaterial());
        policy.setPreviousClaimsHistory(request.getPreviousClaimsHistory());
        policy.setSafetyFeatures(request.getSafetyFeatures());
        
        // When customer submits advanced details, send it to the agent for review
        policy.setStatus("UNDER_REVIEW");

        return mapToResponse(policyRepository.save(policy));
    }
}
