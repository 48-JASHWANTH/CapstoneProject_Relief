package org.hartford.relief.controller.userController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.request.ClaimRequest;
import org.hartford.relief.dto.response.ClaimResponse;
import org.hartford.relief.service.userService.UserClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/claims")
@RequiredArgsConstructor
public class UserClaimController {

    private final UserClaimService userClaimService;

    // POST /api/users/{userId}/claims
    @PostMapping
    public ResponseEntity<ClaimResponse> fileClaim(@PathVariable Long userId,
                                                    @RequestBody ClaimRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userClaimService.fileClaim(userId, request));
    }

    // GET /api/users/{userId}/claims
    @GetMapping
    public ResponseEntity<List<ClaimResponse>> getMyClaims(@PathVariable Long userId) {
        return ResponseEntity.ok(userClaimService.getMyClaims(userId));
    }

    // GET /api/users/{userId}/claims/{claimId}
    @GetMapping("/{claimId}")
    public ResponseEntity<ClaimResponse> getMyClaimById(@PathVariable Long userId,
                                                         @PathVariable Long claimId) {
        return ResponseEntity.ok(userClaimService.getMyClaimById(userId, claimId));
    }
}
