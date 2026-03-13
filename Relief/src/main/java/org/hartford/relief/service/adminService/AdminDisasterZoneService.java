package org.hartford.relief.service.adminService;

import org.hartford.relief.dto.request.DisasterZoneRequest;
import org.hartford.relief.dto.response.DisasterZoneResponse;

import java.util.List;

public interface AdminDisasterZoneService {

    DisasterZoneResponse createDisasterZone(DisasterZoneRequest request);

    List<DisasterZoneResponse> getAllDisasterZones();

    DisasterZoneResponse getDisasterZoneById(Long id);

    DisasterZoneResponse updateDisasterZone(Long id, DisasterZoneRequest request);

    void deleteDisasterZone(Long id);

    List<DisasterZoneResponse> getByRiskLevel(String riskLevel);

    List<DisasterZoneResponse> getByDisasterType(String disasterType);
}
