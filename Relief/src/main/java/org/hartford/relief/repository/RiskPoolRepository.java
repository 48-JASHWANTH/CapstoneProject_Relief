package org.hartford.relief.repository;

import org.hartford.relief.entity.RiskPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskPoolRepository extends JpaRepository<RiskPool, Long> {

    Optional<RiskPool> findByDisasterType(String disasterType);

    List<RiskPool> findByPoolStatus(String poolStatus);

    boolean existsByDisasterType(String disasterType);
}
