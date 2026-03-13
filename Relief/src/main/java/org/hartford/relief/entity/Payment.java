package org.hartford.relief.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Policies (1) → (M) Payments — back side
    @JsonBackReference("policy-payments")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    // Claims (1) → (M) Payments — back side
    @JsonBackReference("claim-payments")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    private Claim claim;

    // RiskPool (1) → (M) Payments — back side
    @JsonBackReference("riskpool-payments")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_pool_id")
    private RiskPool riskPool;

    @Column(name = "payment_type")
    private String paymentType;

    private Double amount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
}
