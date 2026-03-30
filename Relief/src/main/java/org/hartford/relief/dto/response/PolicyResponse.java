package org.hartford.relief.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyResponse {

    private Long id;
    private String policyNumber;
    private Long userId;
    private String userName;
    private Long agentId;
    private String agentName;
    private String disasterType;
    private String policyType;
    private String propertyAddress;
    private Double propertyValue;
    private Double sumInsured;
    private Double premiumAmount;
    private String status;
    private String remarks;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextPremiumDueDate;
    private String region;
    private Integer tenure;
    private Long disasterZoneId;
    private String disasterZoneName;
    private Double disasterZoneRiskFactor;
    private String riskPoolDisasterType;

    private Integer yearBuilt;
    private String constructionMaterial;
    private String previousClaimsHistory;
    private String safetyFeatures;
    private java.util.List<PolicyDocumentResponse> documents;
}
