package org.hartford.relief.repository;

import org.hartford.relief.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByClaimNumber(String claimNumber);

    List<Claim> findByPolicyId(Long policyId);

    List<Claim> findByStatus(String status);

    List<Claim> findByPolicy_UserId(Long userId);

    List<Claim> findByPolicy_AgentId(Long agentId);

    List<Claim> findByAssignedOfficer_Id(Long officerId);

    List<Claim> findByAssignedOfficerIsNull();
}
