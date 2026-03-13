package org.hartford.relief.service.impl.adminServiceImpl;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.AssignRoleRequest;
import org.hartford.relief.dto.request.CreateUserRequest;
import org.hartford.relief.dto.request.UserStatusRequest;
import org.hartford.relief.dto.response.UserResponse;
import org.hartford.relief.entity.Agent;
import org.hartford.relief.entity.Role;
import org.hartford.relief.entity.User;
import org.hartford.relief.exception.BadRequestException;
import org.hartford.relief.exception.DuplicateResourceException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.AgentRepository;
import org.hartford.relief.repository.RoleRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.adminService.AdminUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }
        if (request.getRoleName() == null || request.getRoleName().isBlank()) {
            throw new BadRequestException("A role must be assigned.");
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleName()));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .role(role)
                .build();
        User saved = userRepository.save(user);

        // If the user is assigned the AGENT role, automatically create an Agent record
        if (request.getRoleName().equalsIgnoreCase("AGENT")) {
            if (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
                throw new BadRequestException("License number is required for AGENT role.");
            }
            if (request.getRegion() == null || request.getRegion().isBlank()) {
                throw new BadRequestException("Region is required for AGENT role.");
            }
            if (agentRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new DuplicateResourceException("License number already registered: " + request.getLicenseNumber());
            }
            Agent agent = Agent.builder()
                    .user(saved)
                    .licenseNumber(request.getLicenseNumber())
                    .region(request.getRegion())
                    .build();
            agentRepository.save(agent);
        }

        return mapToResponse(saved);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, UserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setStatus(request.getStatus());
        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserResponse assignRolesToUser(AssignRoleRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleName()));
        user.setRole(role);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getRole() == null || !user.getRole().getName().equals(roleName)) {
            throw new BadRequestException("User does not have role: " + roleName);
        }
        user.setRole(null);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public List<UserResponse> getUsersByRole(String roleName) {
        return userRepository.findByRole_Name(roleName)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByStatus(String status) {
        return userRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }
}
