package org.hartford.relief.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyAdvancedDetailsRequest {
    private Integer yearBuilt;
    private Integer roofAge;
    private String constructionMaterial;
    private String previousClaimsHistory;
    private String safetyFeatures;
}
