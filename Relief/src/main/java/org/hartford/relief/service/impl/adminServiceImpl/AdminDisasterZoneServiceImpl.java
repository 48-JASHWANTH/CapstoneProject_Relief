package org.hartford.relief.service.impl.adminServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.DisasterZoneRequest;
import org.hartford.relief.dto.response.DisasterZoneResponse;
import org.hartford.relief.entity.DisasterZone;
import org.hartford.relief.exception.DuplicateResourceException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.DisasterZoneRepository;
import org.hartford.relief.service.adminService.AdminDisasterZoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDisasterZoneServiceImpl implements AdminDisasterZoneService {

    private final DisasterZoneRepository disasterZoneRepository;

    @Override
    @Transactional
    public DisasterZoneResponse createDisasterZone(DisasterZoneRequest request) {
        if (disasterZoneRepository.existsByZoneName(request.getZoneName())) {
            throw new DuplicateResourceException("Disaster zone already exists with name: " + request.getZoneName());
        }
        DisasterZone zone = DisasterZone.builder()
                .zoneName(request.getZoneName())
                .location(request.getLocation())
                .riskLevel(request.getRiskLevel())
                .disasterType(request.getDisasterType())
                .build();
        return mapToResponse(disasterZoneRepository.save(zone));
    }

    @Override
    public List<DisasterZoneResponse> getAllDisasterZones() {
        return disasterZoneRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DisasterZoneResponse getDisasterZoneById(Long id) {
        DisasterZone zone = disasterZoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DisasterZone", id));
        return mapToResponse(zone);
    }

    @Override
    @Transactional
    public DisasterZoneResponse updateDisasterZone(Long id, DisasterZoneRequest request) {
        DisasterZone zone = disasterZoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DisasterZone", id));

        if (!zone.getZoneName().equals(request.getZoneName())
                && disasterZoneRepository.existsByZoneName(request.getZoneName())) {
            throw new DuplicateResourceException("Disaster zone already exists with name: " + request.getZoneName());
        }
        zone.setZoneName(request.getZoneName());
        zone.setLocation(request.getLocation());
        zone.setRiskLevel(request.getRiskLevel());
        zone.setDisasterType(request.getDisasterType());
        return mapToResponse(disasterZoneRepository.save(zone));
    }

    @Override
    @Transactional
    public void deleteDisasterZone(Long id) {
        if (!disasterZoneRepository.existsById(id)) {
            throw new ResourceNotFoundException("DisasterZone", id);
        }
        disasterZoneRepository.deleteById(id);
    }

    @Override
    public List<DisasterZoneResponse> getByRiskLevel(String riskLevel) {
        return disasterZoneRepository.findByRiskLevel(riskLevel)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DisasterZoneResponse> getByDisasterType(String disasterType) {
        return disasterZoneRepository.findByDisasterType(disasterType)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DisasterZoneResponse mapToResponse(DisasterZone zone) {
        return DisasterZoneResponse.builder()
                .id(zone.getId())
                .zoneName(zone.getZoneName())
                .location(zone.getLocation())
                .riskLevel(zone.getRiskLevel())
                .disasterType(zone.getDisasterType())
                .totalPolicies(zone.getPolicies() != null ? zone.getPolicies().size() : 0)
                .build();
    }
}
