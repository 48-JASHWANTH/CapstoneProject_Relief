package org.hartford.relief.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_number", nullable = false, unique = true)
    private String claimNumber;

    // Policies (1) → (M) Claims — back side
    @JsonBackReference("policy-claims")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    // RiskPool (1) → (M) Claims — back side
    @JsonBackReference("riskpool-claims")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_pool_id")
    private RiskPool riskPool;

    private String description;

    @Column(name = "estimated_loss")
    private Double estimatedLoss;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @Column(name = "damage_type")
    private String damageType;

    @Column(name = "approved_amount")
    private Double approvedAmount;

    private String status;

    @Column(name = "officer_remarks", length = 1000)
    private String officerRemarks;

    @Column(name = "filed_date")
    private LocalDateTime filedDate;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;


    // Claims Officer assigned to handle this claim
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id", nullable = true)
    private User assignedOfficer;

    // Claims (1) → (M) Payments — managed side
    @Builder.Default
    @JsonManagedReference("claim-payments")
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    // Claims (1) → (M) Documents — managed side
    @Builder.Default
    @JsonManagedReference("claim-documents")
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClaimDocument> documents = new ArrayList<>();
}
