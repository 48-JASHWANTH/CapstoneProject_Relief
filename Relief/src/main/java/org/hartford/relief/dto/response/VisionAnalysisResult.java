package org.hartford.relief.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisionAnalysisResult {
    private String damageType;    // Flood | Structural | Fire | Wind | Unknown
    private String severity;      // Minor | Moderate | Severe
    private Double confidence;    // e.g., 87.5
    private String suggestedLoss; // e.g., "₹75,000 – ₹1,50,000"
}
