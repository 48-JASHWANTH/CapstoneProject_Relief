package org.hartford.relief.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("policy-documents")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "document_status")
    private String documentStatus;

    @Column(name = "agent_remarks", length = 500)
    private String agentRemarks;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;
}
