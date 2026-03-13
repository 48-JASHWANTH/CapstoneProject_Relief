package org.hartford.relief.service;

import org.hartford.relief.dto.request.AssignOfficerRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.entity.Claim;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.entity.Role;
import org.hartford.relief.entity.User;
import org.hartford.relief.exception.InvalidRoleException;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.repository.ClaimRepository;
import org.hartford.relief.repository.UserRepository;
import org.hartford.relief.service.impl.adminServiceImpl.AdminClaimServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminClaimServiceImpl adminClaimService;

    private Claim claim;
    private User officer;
    private Policy policy;

    @BeforeEach
    void setUp() {
        Role officerRole = Role.builder().id(1L).name("CLAIMS_OFFICER").build();

        User policyUser = User.builder().id(10L).name("Jane Customer").build();

        policy = Policy.builder()
                .id(5L)
                .policyNumber("POL-001")
                .disasterType("FLOOD")
                .propertyAddress("123 Main St")
                .sumInsured(100000.0)
                .premiumAmount(1200.0)
                .user(policyUser)
                .build();

        officer = User.builder()
                .id(2L)
                .name("John Officer")
                .email("officer@example.com")
                .role(officerRole)
                .build();

        claim = Claim.builder()
                .id(1L)
                .claimNumber("CLM-001")
                .policy(policy)
                .description("Flood damage")
                .estimatedLoss(50000.0)
                .status("FILED")
                .filedDate(LocalDateTime.now())
                .build();
    }

    // ── getAllClaims ─────────────────────────────────────────────────────────

    @Test
    void getAllClaims_returnsListOfClaimResponses() {
        when(claimRepository.findAll()).thenReturn(List.of(claim));

        List<ClaimResponse> result = adminClaimService.getAllClaims();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClaimNumber()).isEqualTo("CLM-001");
        assertThat(result.get(0).getStatus()).isEqualTo("FILED");
        verify(claimRepository, times(1)).findAll();
    }

    @Test
    void getAllClaims_returnsEmptyListWhenNoClaims() {
        when(claimRepository.findAll()).thenReturn(List.of());

        List<ClaimResponse> result = adminClaimService.getAllClaims();

        assertThat(result).isEmpty();
    }

    // ── getClaimById ─────────────────────────────────────────────────────────

    @Test
    void getClaimById_returnsMappedResponse() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));

        ClaimResponse result = adminClaimService.getClaimById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClaimNumber()).isEqualTo("CLM-001");
        assertThat(result.getPolicyId()).isEqualTo(5L);
        assertThat(result.getDisasterType()).isEqualTo("FLOOD");
    }

    @Test
    void getClaimById_throwsResourceNotFoundWhenMissing() {
        when(claimRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminClaimService.getClaimById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Claim");
    }

    // ── getUnassignedClaims ───────────────────────────────────────────────────

    @Test
    void getUnassignedClaims_returnsClaimsWithNoOfficer() {
        when(claimRepository.findByAssignedOfficerIsNull()).thenReturn(List.of(claim));

        List<ClaimResponse> result = adminClaimService.getUnassignedClaims();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssignedOfficerId()).isNull();
        verify(claimRepository).findByAssignedOfficerIsNull();
    }

    // ── getClaimsByStatus ─────────────────────────────────────────────────────

    @Test
    void getClaimsByStatus_returnsMatchingClaims() {
        when(claimRepository.findByStatus("FILED")).thenReturn(List.of(claim));

        List<ClaimResponse> result = adminClaimService.getClaimsByStatus("filed");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("FILED");
        // implementation uppercases the argument before querying
        verify(claimRepository).findByStatus("FILED");
    }

    @Test
    void getClaimsByStatus_returnsEmptyWhenNoneMatch() {
        when(claimRepository.findByStatus("APPROVED")).thenReturn(List.of());

        List<ClaimResponse> result = adminClaimService.getClaimsByStatus("approved");

        assertThat(result).isEmpty();
    }

    // ── assignOfficerToClaim ──────────────────────────────────────────────────

    @Test
    void assignOfficerToClaim_successfullyAssignsOfficer() {
        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setOfficerUserId(2L);

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(userRepository.findById(2L)).thenReturn(Optional.of(officer));
        when(claimRepository.save(claim)).thenReturn(claim);

        ClaimResponse result = adminClaimService.assignOfficerToClaim(1L, request);

        assertThat(result).isNotNull();
        assertThat(claim.getAssignedOfficer()).isEqualTo(officer);
        verify(claimRepository).save(claim);
    }

    @Test
    void assignOfficerToClaim_throwsResourceNotFoundWhenClaimMissing() {
        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setOfficerUserId(2L);

        when(claimRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminClaimService.assignOfficerToClaim(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Claim");
    }

    @Test
    void assignOfficerToClaim_throwsResourceNotFoundWhenOfficerMissing() {
        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setOfficerUserId(99L);

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminClaimService.assignOfficerToClaim(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");
    }

    @Test
    void assignOfficerToClaim_throwsInvalidRoleExceptionWhenUserIsNotOfficer() {
        Role customerRole = Role.builder().id(3L).name("CUSTOMER").build();
        User customer = User.builder().id(5L).name("Bob Customer").role(customerRole).build();

        AssignOfficerRequest request = new AssignOfficerRequest();
        request.setOfficerUserId(5L);

        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(userRepository.findById(5L)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> adminClaimService.assignOfficerToClaim(1L, request))
                .isInstanceOf(InvalidRoleException.class);
    }
}
