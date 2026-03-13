package org.hartford.relief.service;

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
import org.hartford.relief.service.impl.adminServiceImpl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private Role customerRole;
    private Role agentRole;
    private User savedUser;

    @BeforeEach
    void setUp() {
        customerRole = Role.builder().id(1L).name("CUSTOMER").build();
        agentRole    = Role.builder().id(2L).name("AGENT").build();

        savedUser = User.builder()
                .id(10L)
                .name("Alice")
                .email("alice@example.com")
                .password("encoded-pw")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .role(customerRole)
                .build();
    }

    // ── createUser ────────────────────────────────────────────────────────────

    @Test
    void createUser_createsCustomerSuccessfully() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret");
        request.setRoleName("CUSTOMER");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("secret")).thenReturn("encoded-pw");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = adminUserService.createUser(request);

        assertThat(result.getName()).isEqualTo("Alice");
        assertThat(result.getEmail()).isEqualTo("alice@example.com");
        assertThat(result.getRole()).isEqualTo("CUSTOMER");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void createUser_throwsDuplicateResourceExceptionWhenEmailExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("alice@example.com");
        request.setRoleName("CUSTOMER");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(savedUser));

        assertThatThrownBy(() -> adminUserService.createUser(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("alice@example.com");
    }

    @Test
    void createUser_throwsBadRequestWhenRoleNameIsBlank() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@example.com");
        request.setRoleName("");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.createUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("role");
    }

    @Test
    void createUser_throwsResourceNotFoundWhenRoleDoesNotExist() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("new@example.com");
        request.setRoleName("UNKNOWN_ROLE");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("UNKNOWN_ROLE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.createUser(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createUser_createsAgentWithLicenseAndRegion() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Bob Agent");
        request.setEmail("bob@example.com");
        request.setPassword("secret");
        request.setRoleName("AGENT");
        request.setLicenseNumber("LIC-999");
        request.setRegion("North");

        User agentUser = User.builder()
                .id(11L).name("Bob Agent").email("bob@example.com")
                .status("ACTIVE").role(agentRole).createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("AGENT")).thenReturn(Optional.of(agentRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pw");
        when(userRepository.save(any(User.class))).thenReturn(agentUser);
        when(agentRepository.existsByLicenseNumber("LIC-999")).thenReturn(false);
        when(agentRepository.save(any(Agent.class))).thenReturn(new Agent());

        UserResponse result = adminUserService.createUser(request);

        assertThat(result.getRole()).isEqualTo("AGENT");
        verify(agentRepository).save(any(Agent.class));
    }

    @Test
    void createUser_throwsBadRequestWhenAgentLicenseMissing() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("agent2@example.com");
        request.setRoleName("AGENT");
        request.setLicenseNumber("");
        request.setRegion("South");

        when(userRepository.findByEmail("agent2@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("AGENT")).thenReturn(Optional.of(agentRole));
        when(passwordEncoder.encode(any())).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        assertThatThrownBy(() -> adminUserService.createUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("License number");
    }

    // ── getAllUsers ────────────────────────────────────────────────────────────

    @Test
    void getAllUsers_returnsListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(List.of(savedUser));

        List<UserResponse> result = adminUserService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("alice@example.com");
    }

    // ── getUserById ───────────────────────────────────────────────────────────

    @Test
    void getUserById_returnsMappedUser() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(savedUser));

        UserResponse result = adminUserService.getUserById(10L);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    void getUserById_throwsResourceNotFoundWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    // ── updateUserStatus ──────────────────────────────────────────────────────

    @Test
    void updateUserStatus_changesStatusSuccessfully() {
        UserStatusRequest statusRequest = UserStatusRequest.builder().status("INACTIVE").build();
        User updatedUser = User.builder()
                .id(10L).name("Alice").email("alice@example.com")
                .status("INACTIVE").role(customerRole).createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(savedUser)).thenReturn(updatedUser);

        UserResponse result = adminUserService.updateUserStatus(10L, statusRequest);

        assertThat(result.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void updateUserStatus_throwsResourceNotFoundWhenUserMissing() {
        UserStatusRequest statusRequest = UserStatusRequest.builder().status("INACTIVE").build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.updateUserStatus(99L, statusRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deleteUser ────────────────────────────────────────────────────────────

    @Test
    void deleteUser_deletesSuccessfully() {
        when(userRepository.existsById(10L)).thenReturn(true);

        adminUserService.deleteUser(10L);

        verify(userRepository).deleteById(10L);
    }

    @Test
    void deleteUser_throwsResourceNotFoundWhenUserDoesNotExist() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> adminUserService.deleteUser(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
        verify(userRepository, never()).deleteById(any());
    }

    // ── assignRolesToUser ─────────────────────────────────────────────────────

    @Test
    void assignRolesToUser_updatesRoleSuccessfully() {
        AssignRoleRequest request = AssignRoleRequest.builder()
                .userId(10L).roleName("AGENT").build();

        User userWithNewRole = User.builder()
                .id(10L).name("Alice").email("alice@example.com")
                .status("ACTIVE").role(agentRole).createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(savedUser));
        when(roleRepository.findByName("AGENT")).thenReturn(Optional.of(agentRole));
        when(userRepository.save(savedUser)).thenReturn(userWithNewRole);

        UserResponse result = adminUserService.assignRolesToUser(request);

        assertThat(result.getRole()).isEqualTo("AGENT");
    }

    // ── removeRoleFromUser ────────────────────────────────────────────────────

    @Test
    void removeRoleFromUser_removesRoleSuccessfully() {
        User userNoRole = User.builder()
                .id(10L).name("Alice").email("alice@example.com")
                .status("ACTIVE").role(null).createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(savedUser)).thenReturn(userNoRole);

        UserResponse result = adminUserService.removeRoleFromUser(10L, "CUSTOMER");

        assertThat(result.getRole()).isNull();
    }

    @Test
    void removeRoleFromUser_throwsBadRequestWhenRoleDoesNotMatch() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(savedUser));

        assertThatThrownBy(() -> adminUserService.removeRoleFromUser(10L, "AGENT"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("AGENT");
    }

    // ── getUsersByRole ────────────────────────────────────────────────────────

    @Test
    void getUsersByRole_returnsMatchingUsers() {
        when(userRepository.findByRole_Name("CUSTOMER")).thenReturn(List.of(savedUser));

        List<UserResponse> result = adminUserService.getUsersByRole("CUSTOMER");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo("CUSTOMER");
    }

    // ── getUsersByStatus ──────────────────────────────────────────────────────

    @Test
    void getUsersByStatus_returnsMatchingUsers() {
        when(userRepository.findByStatus("ACTIVE")).thenReturn(List.of(savedUser));

        List<UserResponse> result = adminUserService.getUsersByStatus("ACTIVE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void getUsersByStatus_returnsEmptyWhenNoneMatch() {
        when(userRepository.findByStatus("SUSPENDED")).thenReturn(List.of());

        List<UserResponse> result = adminUserService.getUsersByStatus("SUSPENDED");

        assertThat(result).isEmpty();
    }
}
