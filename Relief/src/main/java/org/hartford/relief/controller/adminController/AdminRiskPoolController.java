package org.hartford.relief.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.RiskPoolRequest;
import org.hartford.relief.dto.response.RiskPoolResponse;
import org.hartford.relief.service.adminService.AdminRiskPoolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/risk-pools")
@RequiredArgsConstructor
public class AdminRiskPoolController {

    private final AdminRiskPoolService adminRiskPoolService;

    // POST /api/admin/risk-pools
    @PostMapping
    public ResponseEntity<RiskPoolResponse> createRiskPool(@RequestBody RiskPoolRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminRiskPoolService.createRiskPool(request));
    }

    // GET /api/admin/risk-pools
    @GetMapping
    public ResponseEntity<List<RiskPoolResponse>> getAllRiskPools() {
        return ResponseEntity.ok(adminRiskPoolService.getAllRiskPools());
    }

    // GET /api/admin/risk-pools/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RiskPoolResponse> getRiskPoolById(@PathVariable Long id) {
        return ResponseEntity.ok(adminRiskPoolService.getRiskPoolById(id));
    }

    // PUT /api/admin/risk-pools/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RiskPoolResponse> updateRiskPool(@PathVariable Long id,
                                                            @RequestBody RiskPoolRequest request) {
        return ResponseEntity.ok(adminRiskPoolService.updateRiskPool(id, request));
    }

    // DELETE /api/admin/risk-pools/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRiskPool(@PathVariable Long id) {
        adminRiskPoolService.deleteRiskPool(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/admin/risk-pools/critical
    @GetMapping("/critical")
    public ResponseEntity<List<RiskPoolResponse>> getCriticalRiskPools() {
        return ResponseEntity.ok(adminRiskPoolService.getCriticalRiskPools());
    }

    // PATCH /api/admin/risk-pools/{id}/evaluate-threshold
    @PatchMapping("/{id}/evaluate-threshold")
    public ResponseEntity<RiskPoolResponse> evaluateThreshold(@PathVariable Long id) {
        return ResponseEntity.ok(adminRiskPoolService.evaluateThreshold(id));
    }

    // GET /api/admin/risk-pools/disaster-types
    @GetMapping("/disaster-types")
    public ResponseEntity<List<String>> getDisasterTypes() {
        return ResponseEntity.ok(adminRiskPoolService.getDisasterTypes());
    }
}
