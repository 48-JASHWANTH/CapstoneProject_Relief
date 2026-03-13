package org.hartford.relief.repository;

import org.hartford.relief.entity.DisasterZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisasterZoneRepository extends JpaRepository<DisasterZone, Long> {

    Optional<DisasterZone> findByZoneName(String zoneName);

    List<DisasterZone> findByRiskLevel(String riskLevel);

    List<DisasterZone> findByDisasterType(String disasterType);

    Optional<DisasterZone> findByDisasterTypeAndLocation(String disasterType, String location);

    boolean existsByZoneName(String zoneName);
}
