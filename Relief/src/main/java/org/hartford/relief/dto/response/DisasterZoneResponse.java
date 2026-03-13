package org.hartford.relief.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterZoneResponse {

    private Long id;
    private String zoneName;
    private String location;
    private String riskLevel;
    private String disasterType;
    private Double riskFactor;
    private int totalPolicies;
}
