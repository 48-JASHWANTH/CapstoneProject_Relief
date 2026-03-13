package org.hartford.relief.service.adminService;

import org.hartford.relief.dto.request.RoleRequest;
import org.hartford.relief.dto.response.RoleResponse;

import java.util.List;

public interface AdminRoleService {

    RoleResponse createRole(RoleRequest request);

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}
