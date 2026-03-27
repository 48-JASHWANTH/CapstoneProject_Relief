package org.hartford.relief.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyDocumentResponse {
    private Long id;
    private Long policyId;
    private String documentType;
    private String fileUrl;
    private String documentStatus;
    private String agentRemarks;
    private LocalDateTime uploadedAt;
}
