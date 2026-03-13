package org.hartford.relief.service.impl.adminServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.response.AdminDashboardResponse;
import org.hartford.relief.entity.RiskPool;
import org.hartford.relief.repository.*;
import org.hartford.relief.service.adminService.AdminDashboardService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final PaymentRepository paymentRepository;
    private final DisasterZoneRepository disasterZoneRepository;
    private final RiskPoolRepository riskPoolRepository;

    @Override
    public AdminDashboardResponse getDashboardStats() {

        long totalUsers        = userRepository.count();
        long totalAgents       = agentRepository.count();
        long totalPolicies     = policyRepository.count();
        long totalClaims       = claimRepository.count();
        long totalPayments     = paymentRepository.count();
        long totalDisasterZones = disasterZoneRepository.count();
        long totalRiskPools    = riskPoolRepository.count();

        long activePolicies  = policyRepository.findByStatus("ACTIVE").size();
        long pendingPolicies = policyRepository.findByStatus("PENDING").size();
        long approvedClaims  = claimRepository.findByStatus("APPROVED").size();
        long pendingClaims   = claimRepository.findByStatus("FILED").size();

        // policies grouped by disasterType
        Map<String, Long> policiesByDisasterType = policyRepository.findAll()
                .stream()
                .filter(p -> p.getDisasterType() != null)
                .collect(Collectors.groupingBy(p -> p.getDisasterType(), Collectors.counting())); // grouped by disasterType

        // claims grouped by status
        Map<String, Long> claimsByStatus = claimRepository.findAll()
                .stream()
                .filter(c -> c.getStatus() != null)
                .collect(Collectors.groupingBy(c -> c.getStatus(), Collectors.counting()));

        // risk pool: disasterType -> totalPremiumCollected
        Map<String, Double> riskPoolSummary = riskPoolRepository.findAll()
                .stream()
                .filter(r -> r.getDisasterType() != null)
                .collect(Collectors.toMap(
                        RiskPool::getDisasterType,
                        r -> r.getTotalPremiumCollected() != null ? r.getTotalPremiumCollected() : 0.0
                ));

        long criticalRiskPools = riskPoolRepository.findByPoolStatus("CRITICAL").size();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalAgents(totalAgents)
                .totalPolicies(totalPolicies)
                .totalClaims(totalClaims)
                .totalPayments(totalPayments)
                .totalDisasterZones(totalDisasterZones)
                .totalRiskPools(totalRiskPools)
                .activePolicies(activePolicies)
                .pendingPolicies(pendingPolicies)
                .approvedClaims(approvedClaims)
                .pendingClaims(pendingClaims)
                .policiesByDisasterType(policiesByDisasterType)
                .claimsByStatus(claimsByStatus)
                .riskPoolSummary(riskPoolSummary)
                .criticalRiskPools(criticalRiskPools)
                .build();
    }
}
