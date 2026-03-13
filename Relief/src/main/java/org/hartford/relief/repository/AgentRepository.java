package org.hartford.relief.repository;

import org.hartford.relief.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByUserId(Long userId);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Agent> findByRegion(String region);
}
