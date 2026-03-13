package org.hartford.relief.service.impl.adminServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.RiskPoolRequest;
import org.hartford.relief.dto.response.RiskPoolResponse;
import org.hartford.relief.entity.RiskPool;
import org.hartford.relief.exception.DuplicateResourceException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.RiskPoolRepository;
import org.hartford.relief.service.adminService.AdminRiskPoolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AdminRiskPoolServiceImpl implements AdminRiskPoolService {

    private final RiskPoolRepository riskPoolRepository;

    @Override
    @Transactional
    public RiskPoolResponse createRiskPool(RiskPoolRequest request) {
        if (riskPoolRepository.existsByDisasterType(request.getDisasterType())) {
            throw new DuplicateResourceException("Risk pool already exists for disaster type: " + request.getDisasterType());
        }
        RiskPool pool = RiskPool.builder()
                .disasterType(request.getDisasterType())
                .totalPremiumCollected(request.getTotalPremiumCollected() != null ? request.getTotalPremiumCollected() : 0.0)
                .totalClaimsPaid(request.getTotalClaimsPaid() != null ? request.getTotalClaimsPaid() : 0.0)
                .thresholdPercentage(request.getThresholdPercentage())
                .poolStatus(request.getPoolStatus() != null ? request.getPoolStatus() : "ACTIVE")
                .build();
        return mapToResponse(riskPoolRepository.save(pool));
    }

    @Override
    public List<RiskPoolResponse> getAllRiskPools() {
        return riskPoolRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RiskPoolResponse getRiskPoolById(Long id) {
        RiskPool pool = riskPoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskPool", id));
        return mapToResponse(pool);
    }

    @Override
    @Transactional
    public RiskPoolResponse updateRiskPool(Long id, RiskPoolRequest request) {
        RiskPool pool = riskPoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskPool", id));

        if (!pool.getDisasterType().equals(request.getDisasterType())
                && riskPoolRepository.existsByDisasterType(request.getDisasterType())) {
            throw new DuplicateResourceException("Risk pool already exists for disaster type: " + request.getDisasterType());
        }
        pool.setDisasterType(request.getDisasterType());
        pool.setTotalPremiumCollected(request.getTotalPremiumCollected());
        pool.setTotalClaimsPaid(request.getTotalClaimsPaid());
        pool.setThresholdPercentage(request.getThresholdPercentage());
        pool.setPoolStatus(request.getPoolStatus());
        return mapToResponse(riskPoolRepository.save(pool));
    }

    @Override
    @Transactional
    public void deleteRiskPool(Long id) {
        if (!riskPoolRepository.existsById(id)) {
            throw new ResourceNotFoundException("RiskPool", id);
        }
        riskPoolRepository.deleteById(id);
    }

    @Override
    public List<RiskPoolResponse> getCriticalRiskPools() {
        return riskPoolRepository.findByPoolStatus("CRITICAL")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RiskPoolResponse evaluateThreshold(Long id) {
        RiskPool pool = riskPoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskPool", id));
        // criticalFlag is computed dynamically by isCritical() — no status change needed
        return mapToResponse(pool);
    }

    @Override
    public List<String> getDisasterTypes() {
        return riskPoolRepository.findAll()
                .stream()
                .map(RiskPool::getDisasterType)
                .filter(t -> t != null)
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isCritical(RiskPool pool) {
        if (pool.getTotalPremiumCollected() == null || pool.getTotalPremiumCollected() == 0
                || pool.getTotalClaimsPaid() == null || pool.getThresholdPercentage() == null) {
            return false;
        }
        double ratio = (pool.getTotalClaimsPaid() / pool.getTotalPremiumCollected()) * 100;
        return ratio >= pool.getThresholdPercentage();
    }

    private RiskPoolResponse mapToResponse(RiskPool pool) {
        return RiskPoolResponse.builder()
                .id(pool.getId())
                .disasterType(pool.getDisasterType())
                .totalPremiumCollected(pool.getTotalPremiumCollected())
                .totalClaimsPaid(pool.getTotalClaimsPaid())
                .thresholdPercentage(pool.getThresholdPercentage())
                .poolStatus(pool.getPoolStatus())
                .criticalFlag(isCritical(pool))
                .totalPolicies(pool.getPolicies() != null ? pool.getPolicies().size() : 0)
                .totalClaims(pool.getClaims() != null ? pool.getClaims().size() : 0)
                .build();
    }
}
