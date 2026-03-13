package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterZoneRequest {

    private String zoneName;
    private String location;
    private String riskLevel;
    private String disasterType;
}
