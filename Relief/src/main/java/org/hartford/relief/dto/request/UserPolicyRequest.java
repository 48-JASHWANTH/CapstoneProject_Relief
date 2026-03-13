package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPolicyRequest {

    private String disasterType;   // e.g. FLOOD, EARTHQUAKE, CYCLONE, WILDFIRE
    private String policyType;     // e.g. BASIC, STANDARD, PREMIUM
    private String propertyAddress;
    private Double propertyValue;
    private Double sumInsured;
    private String region;         // e.g. NORTH, SOUTH, EAST, WEST, CENTRAL
    private Integer tenure;        // policy duration in years
    private Double premiumAmount;  // customer-entered premium amount
}
