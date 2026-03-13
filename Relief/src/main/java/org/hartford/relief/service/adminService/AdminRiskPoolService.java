package org.hartford.relief.service.adminService;

import org.hartford.relief.dto.request.RiskPoolRequest;
import org.hartford.relief.dto.response.RiskPoolResponse;

import java.util.List;

public interface AdminRiskPoolService {

    RiskPoolResponse createRiskPool(RiskPoolRequest request);

    List<RiskPoolResponse> getAllRiskPools();

    RiskPoolResponse getRiskPoolById(Long id);

    RiskPoolResponse updateRiskPool(Long id, RiskPoolRequest request);

    void deleteRiskPool(Long id);

    List<RiskPoolResponse> getCriticalRiskPools();

    RiskPoolResponse evaluateThreshold(Long id);

    List<String> getDisasterTypes();
}
