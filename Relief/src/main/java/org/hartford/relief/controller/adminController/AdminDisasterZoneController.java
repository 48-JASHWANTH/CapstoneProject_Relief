package org.hartford.relief.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.DisasterZoneRequest;
import org.hartford.relief.dto.response.DisasterZoneResponse;
import org.hartford.relief.service.adminService.AdminDisasterZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/disaster-zones")
@RequiredArgsConstructor
public class AdminDisasterZoneController {

    private final AdminDisasterZoneService adminDisasterZoneService;

    // POST /api/admin/disaster-zones
    @PostMapping
    public ResponseEntity<DisasterZoneResponse> createDisasterZone(@RequestBody DisasterZoneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminDisasterZoneService.createDisasterZone(request));
    }

    // GET /api/admin/disaster-zones
    @GetMapping
    public ResponseEntity<List<DisasterZoneResponse>> getAllDisasterZones() {
        return ResponseEntity.ok(adminDisasterZoneService.getAllDisasterZones());
    }

    // GET /api/admin/disaster-zones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DisasterZoneResponse> getDisasterZoneById(@PathVariable Long id) {
        return ResponseEntity.ok(adminDisasterZoneService.getDisasterZoneById(id));
    }

    // PUT /api/admin/disaster-zones/{id}
    @PutMapping("/{id}")
    public ResponseEntity<DisasterZoneResponse> updateDisasterZone(@PathVariable Long id,
                                                                    @RequestBody DisasterZoneRequest request) {
        return ResponseEntity.ok(adminDisasterZoneService.updateDisasterZone(id, request));
    }

    // DELETE /api/admin/disaster-zones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDisasterZone(@PathVariable Long id) {
        adminDisasterZoneService.deleteDisasterZone(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/admin/disaster-zones/by-risk-level?riskLevel=HIGH
    @GetMapping("/by-risk-level")
    public ResponseEntity<List<DisasterZoneResponse>> getByRiskLevel(@RequestParam String riskLevel) {
        return ResponseEntity.ok(adminDisasterZoneService.getByRiskLevel(riskLevel));
    }

    // GET /api/admin/disaster-zones/by-disaster-type?disasterType=FLOOD
    @GetMapping("/by-disaster-type")
    public ResponseEntity<List<DisasterZoneResponse>> getByDisasterType(@RequestParam String disasterType) {
        return ResponseEntity.ok(adminDisasterZoneService.getByDisasterType(disasterType));
    }
}
