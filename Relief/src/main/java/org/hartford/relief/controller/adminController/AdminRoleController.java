package org.hartford.relief.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.RoleRequest;
import org.hartford.relief.dto.response.RoleResponse;
import org.hartford.relief.service.adminService.AdminRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    // POST /api/admin/roles
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminRoleService.createRole(request));
    }

    // GET /api/admin/roles
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(adminRoleService.getAllRoles());
    }

    // GET /api/admin/roles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(adminRoleService.getRoleById(id));
    }

    // PUT /api/admin/roles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id,
                                                    @RequestBody RoleRequest request) {
        return ResponseEntity.ok(adminRoleService.updateRole(id, request));
    }

    // DELETE /api/admin/roles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        adminRoleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
