package org.hartford.relief.controller;

import org.hartford.relief.controller.adminController.AdminUserController;
import org.hartford.relief.dto.request.AssignRoleRequest;
import org.hartford.relief.dto.request.CreateUserRequest;
import org.hartford.relief.dto.request.UserStatusRequest;
import org.hartford.relief.dto.response.UserResponse;
import org.hartford.relief.exception.BadRequestException;
import org.hartford.relief.exception.DuplicateResourceException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.service.adminService.AdminUserService;
import org.hartford.relief.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@WithMockUser(roles = "ADMIN")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminUserService adminUserService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .id(10L)
                .name("Alice")
                .email("alice@example.com")
                .status("ACTIVE")
                .role("CUSTOMER")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── POST /api/admin/users ─────────────────────────────────────────────────

    @Test
    void createUser_returns201OnSuccess() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret");
        request.setRoleName("CUSTOMER");

        when(adminUserService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"password\":\"secret\",\"roleName\":\"CUSTOMER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void createUser_returns409WhenEmailAlreadyExists() throws Exception {
        when(adminUserService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new DuplicateResourceException("Email already registered: alice@example.com"));

        mockMvc.perform(post("/api/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@example.com\",\"roleName\":\"CUSTOMER\"}"))
                .andExpect(status().isConflict());
    }

    // ── GET /api/admin/users ──────────────────────────────────────────────────

    @Test
    void getAllUsers_returns200WithList() throws Exception {
        when(adminUserService.getAllUsers()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }

    @Test
    void getAllUsers_returns200WithEmptyList() throws Exception {
        when(adminUserService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/admin/users/{id} ─────────────────────────────────────────────

    @Test
    void getUserById_returns200WhenFound() throws Exception {
        when(adminUserService.getUserById(10L)).thenReturn(userResponse);

        mockMvc.perform(get("/api/admin/users/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getUserById_returns404WhenNotFound() throws Exception {
        when(adminUserService.getUserById(99L))
                .thenThrow(new ResourceNotFoundException("User", 99L));

        mockMvc.perform(get("/api/admin/users/99"))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /api/admin/users/{id}/status ────────────────────────────────────

    @Test
    void updateUserStatus_returns200WithUpdatedUser() throws Exception {
        UserResponse inactiveUser = UserResponse.builder()
                .id(10L).name("Alice").email("alice@example.com")
                .status("INACTIVE").role("CUSTOMER").build();

        // UserStatusRequest statusRequest = UserStatusRequest.builder().status("INACTIVE").build();

        when(adminUserService.updateUserStatus(eq(10L), any(UserStatusRequest.class)))
                .thenReturn(inactiveUser);

        mockMvc.perform(patch("/api/admin/users/10/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    // ── DELETE /api/admin/users/{id} ─────────────────────────────────────────

    @Test
    void deleteUser_returns204OnSuccess() throws Exception {
        doNothing().when(adminUserService).deleteUser(10L);

        mockMvc.perform(delete("/api/admin/users/10").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_returns404WhenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User", 99L)).when(adminUserService).deleteUser(99L);

        mockMvc.perform(delete("/api/admin/users/99").with(csrf()))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/admin/users/assign-roles ────────────────────────────────────

    @Test
    void assignRoles_returns200WithUpdatedUser() throws Exception {
        UserResponse agentUser = UserResponse.builder()
                .id(10L).name("Alice").email("alice@example.com")
                .status("ACTIVE").role("AGENT").build();

        // AssignRoleRequest request = AssignRoleRequest.builder()
        //         .userId(10L).roleName("AGENT").build();

        when(adminUserService.assignRolesToUser(any(AssignRoleRequest.class))).thenReturn(agentUser);

        mockMvc.perform(post("/api/admin/users/assign-roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":10,\"roleName\":\"AGENT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("AGENT"));
    }

    // ── DELETE /api/admin/users/{id}/roles/{roleName} ─────────────────────────

    @Test
    void removeRole_returns200WhenRoleRemoved() throws Exception {
        UserResponse noRoleUser = UserResponse.builder()
                .id(10L).name("Alice").email("alice@example.com")
                .status("ACTIVE").role(null).build();

        when(adminUserService.removeRoleFromUser(10L, "CUSTOMER")).thenReturn(noRoleUser);

        mockMvc.perform(delete("/api/admin/users/10/roles/CUSTOMER").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void removeRole_returns400WhenRoleDoesNotMatch() throws Exception {
        when(adminUserService.removeRoleFromUser(10L, "AGENT"))
                .thenThrow(new BadRequestException("User does not have role: AGENT"));

        mockMvc.perform(delete("/api/admin/users/10/roles/AGENT").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/admin/users/by-role ──────────────────────────────────────────

    @Test
    void getUsersByRole_returns200WithMatchingUsers() throws Exception {
        when(adminUserService.getUsersByRole("CUSTOMER")).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/admin/users/by-role").param("roleName", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("CUSTOMER"));
    }

    // ── GET /api/admin/users/by-status ────────────────────────────────────────

    @Test
    void getUsersByStatus_returns200WithMatchingUsers() throws Exception {
        when(adminUserService.getUsersByStatus("ACTIVE")).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/admin/users/by-status").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }
}
