package org.hartford.relief.repository;

import org.hartford.relief.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    List<Policy> findByUserId(Long userId);

    List<Policy> findByAgentId(Long agentId);

    List<Policy> findByStatus(String status);

    List<Policy> findByDisasterType(String disasterType);

    List<Policy> findByUserIdAndStatus(Long userId, String status);
}
