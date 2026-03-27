package org.hartford.relief.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDocumentResponse {
    private Long id;
    private Long claimId;
    private String documentType;
    private String fileUrl;
    private String documentStatus;
    private String officerRemarks;
    private LocalDateTime uploadedAt;
}
