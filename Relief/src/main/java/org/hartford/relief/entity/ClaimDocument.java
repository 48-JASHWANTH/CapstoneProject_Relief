package org.hartford.relief.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("claim-documents")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Column(name = "document_type", nullable = false)
    private String documentType; // e.g., 'DAMAGE_PHOTO', 'POLICE_REPORT'

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "document_status", nullable = false)
    private String documentStatus; // 'PENDING', 'APPROVED', 'REJECTED'

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "officer_remarks", length = 1000)
    private String officerRemarks;

    // --- Vertex AI Vision Analysis Fields ---
    @Column(name = "ai_damage_type")
    private String aiDamageType;   // e.g., "Flood"

    @Column(name = "ai_severity")
    private String aiSeverity;     // "Minor" | "Moderate" | "Severe"

    @Column(name = "ai_confidence")
    private Double aiConfidence;   // e.g., 87.5

    @Column(name = "ai_suggested_loss")
    private String aiSuggestedLoss; // e.g., "₹75,000 – ₹1,50,000"

    @Column(name = "ai_summary", length = 2000)
    private String aiSummary;       // Groq AI summary for text documents
}
