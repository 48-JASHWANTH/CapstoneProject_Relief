package org.hartford.relief.service.impl.adminServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.RoleRequest;
import org.hartford.relief.dto.response.RoleResponse;
import org.hartford.relief.entity.Role;
import org.hartford.relief.exception.DuplicateResourceException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.RoleRepository;
import org.hartford.relief.service.adminService.AdminRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role already exists with name: " + request.getName());
        }
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return mapToResponse(roleRepository.save(role));
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return mapToResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Role already exists with name: " + request.getName());
        }
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        return mapToResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", id);
        }
        roleRepository.deleteById(id);
    }

    private RoleResponse mapToResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
