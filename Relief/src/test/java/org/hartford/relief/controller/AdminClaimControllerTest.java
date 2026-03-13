package org.hartford.relief.controller;

import org.hartford.relief.controller.adminController.AdminClaimController;
import org.hartford.relief.dto.request.AssignOfficerRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.service.adminService.AdminClaimService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminClaimController.class)
@WithMockUser(roles = "ADMIN")
class AdminClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminClaimService adminClaimService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private ClaimResponse claimResponse;

    @BeforeEach
    void setUp() {
        claimResponse = ClaimResponse.builder()
                .id(1L)
                .claimNumber("CLM-001")
                .policyId(5L)
                .policyNumber("POL-001")
                .disasterType("FLOOD")
                .description("Flood damage")
                .estimatedLoss(50000.0)
                .status("FILED")
                .filedDate(LocalDateTime.now())
                .build();
    }

    // ── GET /api/admin/claims ─────────────────────────────────────────────────

    @Test
    void getAllClaims_returns200WithList() throws Exception {
        when(adminClaimService.getAllClaims()).thenReturn(List.of(claimResponse));

        mockMvc.perform(get("/api/admin/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].claimNumber").value("CLM-001"))
                .andExpect(jsonPath("$[0].status").value("FILED"));
    }

    @Test
    void getAllClaims_returns200WithEmptyList() throws Exception {
        when(adminClaimService.getAllClaims()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/admin/claims/unassigned ──────────────────────────────────────

    @Test
    void getUnassignedClaims_returns200WithUnassignedList() throws Exception {
        when(adminClaimService.getUnassignedClaims()).thenReturn(List.of(claimResponse));

        mockMvc.perform(get("/api/admin/claims/unassigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].claimNumber").value("CLM-001"));
    }

    // ── GET /api/admin/claims/{id} ────────────────────────────────────────────

    @Test
    void getClaimById_returns200WhenFound() throws Exception {
        when(adminClaimService.getClaimById(1L)).thenReturn(claimResponse);

        mockMvc.perform(get("/api/admin/claims/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.disasterType").value("FLOOD"));
    }

    @Test
    void getClaimById_returns404WhenNotFound() throws Exception {
        when(adminClaimService.getClaimById(99L))
                .thenThrow(new ResourceNotFoundException("Claim", 99L));

        mockMvc.perform(get("/api/admin/claims/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/admin/claims/by-status ───────────────────────────────────────

    @Test
    void getClaimsByStatus_returns200WithMatchingClaims() throws Exception {
        when(adminClaimService.getClaimsByStatus("FILED")).thenReturn(List.of(claimResponse));

        mockMvc.perform(get("/api/admin/claims/by-status").param("status", "FILED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("FILED"));
    }

    // ── PATCH /api/admin/claims/{id}/assign-officer ───────────────────────────

    @Test
    void assignOfficer_returns200OnSuccess() throws Exception {
        ClaimResponse assignedResponse = ClaimResponse.builder()
                .id(1L)
                .claimNumber("CLM-001")
                .assignedOfficerId(2L)
                .assignedOfficerName("John Officer")
                .status("FILED")
                .build();

        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setOfficerUserId(2L);

        when(adminClaimService.assignOfficerToClaim(eq(1L), any(AssignOfficerRequest.class)))
                .thenReturn(assignedResponse);

        mockMvc.perform(patch("/api/admin/claims/1/assign-officer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"officerUserId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedOfficerId").value(2))
                .andExpect(jsonPath("$.assignedOfficerName").value("John Officer"));
    }

    @Test
    void assignOfficer_returns404WhenClaimNotFound() throws Exception {
        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setOfficerUserId(2L);

        when(adminClaimService.assignOfficerToClaim(eq(99L), any(AssignOfficerRequest.class)))
                .thenThrow(new ResourceNotFoundException("Claim", 99L));

        mockMvc.perform(patch("/api/admin/claims/99/assign-officer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"officerUserId\":2}"))
                .andExpect(status().isNotFound());
    }
}
