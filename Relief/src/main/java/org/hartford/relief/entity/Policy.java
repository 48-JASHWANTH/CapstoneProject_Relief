package org.hartford.relief.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", nullable = false, unique = true)
    private String policyNumber;

    // Users (1) → (M) Policies — back side
    @JsonBackReference("user-policies")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Agents (1) → (M) Policies — back side
    @JsonBackReference("agent-policies")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = true)
    private Agent agent;

    // DisasterZone (1) → (M) Policies — back side
    @JsonBackReference("disasterzone-policies")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disaster_zone_id")
    private DisasterZone disasterZone;

    // RiskPool (1) → (M) Policies — back side
    @JsonBackReference("riskpool-policies")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_pool_id")
    private RiskPool riskPool;

    @Column(name = "disaster_type")
    private String disasterType;

    private String region;

    private Integer tenure;

    @Column(name = "policy_type")
    private String policyType;

    @Column(name = "property_address")
    private String propertyAddress;

    @Column(name = "property_value")
    private Double propertyValue;

    @Column(name = "sum_insured")
    private Double sumInsured;

    @Column(name = "premium_amount")
    private Double premiumAmount;

    private String status;

    // Underwriter notes / admin remarks
    @Column(length = 1000)
    private String remarks;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "next_premium_due_date")
    private LocalDate nextPremiumDueDate;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "construction_material")
    private String constructionMaterial;

    @Column(name = "previous_claims_history", length = 1000)
    private String previousClaimsHistory;

    @Column(name = "safety_features", length = 500)
    private String safetyFeatures;

    @JsonManagedReference("policy-documents")
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PolicyDocument> documents = new ArrayList<>();

    // Policies (1) → (M) Claims — managed side
    @JsonManagedReference("policy-claims")
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Claim> claims = new ArrayList<>();

    // Policies (1) → (M) Payments — managed side
    @JsonManagedReference("policy-payments")
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}
