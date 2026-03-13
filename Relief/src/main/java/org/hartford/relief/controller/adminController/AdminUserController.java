package org.hartford.relief.controller.adminController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AssignRoleRequest;
import org.hartford.relief.dto.request.CreateUserRequest;
import org.hartford.relief.dto.request.UserStatusRequest;
import org.hartford.relief.dto.response.UserResponse;
import org.hartford.relief.service.adminService.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // POST /api/admin/users — create user with role(s)
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserService.createUser(request));
    }

    // GET /api/admin/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // GET /api/admin/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    // PATCH /api/admin/users/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id,
                                                          @RequestBody UserStatusRequest request) {
        return ResponseEntity.ok(adminUserService.updateUserStatus(id, request));
    }

    // DELETE /api/admin/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/admin/users/assign-roles
    @PostMapping("/assign-roles")
    public ResponseEntity<UserResponse> assignRoles(@RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(adminUserService.assignRolesToUser(request));
    }

    // DELETE /api/admin/users/{id}/roles/{roleName}
    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<UserResponse> removeRole(@PathVariable Long id,
                                                    @PathVariable String roleName) {
        return ResponseEntity.ok(adminUserService.removeRoleFromUser(id, roleName));
    }

    // GET /api/admin/users/by-role?roleName=AGENT
    @GetMapping("/by-role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam String roleName) {
        return ResponseEntity.ok(adminUserService.getUsersByRole(roleName));
    }

    // GET /api/admin/users/by-status?status=ACTIVE
    @GetMapping("/by-status")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(@RequestParam String status) {
        return ResponseEntity.ok(adminUserService.getUsersByStatus(status));
    }
}
