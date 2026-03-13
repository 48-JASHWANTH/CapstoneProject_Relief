package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentRequest {

    private Long userId;
    private String licenseNumber;
    private String region;
}
