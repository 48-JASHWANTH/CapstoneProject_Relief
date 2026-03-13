package org.hartford.relief.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AgentRequest;
import org.hartford.relief.dto.response.AgentResponse;
import org.hartford.relief.service.adminService.AdminAgentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agents")
@RequiredArgsConstructor
public class AdminAgentController {

    private final AdminAgentService adminAgentService;

    // POST /api/admin/agents
    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(@RequestBody AgentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminAgentService.createAgent(request));
    }

    // GET /api/admin/agents
    @GetMapping
    public ResponseEntity<List<AgentResponse>> getAllAgents() {
        return ResponseEntity.ok(adminAgentService.getAllAgents());
    }

    // GET /api/admin/agents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AgentResponse> getAgentById(@PathVariable Long id) {
        return ResponseEntity.ok(adminAgentService.getAgentById(id));
    }

    // PUT /api/admin/agents/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AgentResponse> updateAgent(@PathVariable Long id,
                                                      @RequestBody AgentRequest request) {
        return ResponseEntity.ok(adminAgentService.updateAgent(id, request));
    }

    // DELETE /api/admin/agents/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        adminAgentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/admin/agents/by-region?region=NORTH
    @GetMapping("/by-region")
    public ResponseEntity<List<AgentResponse>> getAgentsByRegion(@RequestParam String region) {
        return ResponseEntity.ok(adminAgentService.getAgentsByRegion(region));
    }
}
