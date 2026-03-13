package org.hartford.relief.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "risk_pools")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "disaster_type")
    private String disasterType;

    @Column(name = "total_premium_collected")
    private Double totalPremiumCollected;

    @Column(name = "total_claims_paid")
    private Double totalClaimsPaid;

    @Column(name = "threshold_percentage")
    private Double thresholdPercentage;

    @Column(name = "pool_status")
    private String poolStatus;

    // RiskPool (1) → (M) Policies
    @Builder.Default
    @JsonManagedReference("riskpool-policies")
    @OneToMany(mappedBy = "riskPool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();

    // RiskPool (1) → (M) Claims
    @Builder.Default
    @JsonManagedReference("riskpool-claims")
    @OneToMany(mappedBy = "riskPool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims = new ArrayList<>();

    // RiskPool (1) → (M) Payments
    @Builder.Default
    @JsonManagedReference("riskpool-payments")
    @OneToMany(mappedBy = "riskPool", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
}
