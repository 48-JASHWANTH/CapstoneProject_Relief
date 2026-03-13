package org.hartford.relief.controller.userController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.response.UserDashboardResponse;
import org.hartford.relief.service.userService.UserDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/dashboard")
@RequiredArgsConstructor
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    // GET /api/users/{userId}/dashboard
    @GetMapping
    public ResponseEntity<UserDashboardResponse> getMyDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(userDashboardService.getMyDashboard(userId));
    }
}
