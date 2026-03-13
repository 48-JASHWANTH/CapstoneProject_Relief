package org.hartford.relief.service.adminService;

import org.hartford.relief.dto.request.AssignRoleRequest;
import org.hartford.relief.dto.request.CreateUserRequest;
import org.hartford.relief.dto.request.UserStatusRequest;
import org.hartford.relief.dto.response.UserResponse;

import java.util.List;

public interface AdminUserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUserStatus(Long id, UserStatusRequest request);

    void deleteUser(Long id);

    UserResponse assignRolesToUser(AssignRoleRequest request);

    UserResponse removeRoleFromUser(Long userId, String roleName);

    List<UserResponse> getUsersByRole(String roleName);

    List<UserResponse> getUsersByStatus(String status);
}
