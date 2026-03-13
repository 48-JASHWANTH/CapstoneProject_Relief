package org.hartford.relief.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String licenseNumber;
    private String region;
    private int totalPolicies;
}
