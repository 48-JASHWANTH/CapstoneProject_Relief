package org.hartford.relief.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "disaster_zones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone_name")
    private String zoneName;

    private String location;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "disaster_type")
    private String disasterType;

    @Column(name = "risk_factor")
    private Double riskFactor;

    // DisasterZone (1) → (M) Policies
    @Builder.Default
    @JsonManagedReference("disasterzone-policies")
    @OneToMany(mappedBy = "disasterZone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();
}
