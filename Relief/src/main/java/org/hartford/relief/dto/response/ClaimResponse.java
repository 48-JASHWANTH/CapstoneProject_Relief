package org.hartford.relief.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponse {

    private Long id;
    private String claimNumber;
    private Long policyId;
    private String policyNumber;
    private String disasterType;
    private String description;
    private Double estimatedLoss;
    private Double approvedAmount;
    private String status;
    private String officerRemarks;
    private LocalDateTime filedDate;
    private LocalDateTime resolvedDate;
    private Long assignedOfficerId;
    private String assignedOfficerName;
    private String propertyAddress;
    private Double sumInsured;
    private Double premiumAmount;
    private String userName;
    private String region;
}
