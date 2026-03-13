package org.hartford.relief.controller.userController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/policy-options")
public class PolicyOptionsController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPolicyOptions() {
        Map<String, Object> options = Map.of(
                "disasterTypes", List.of("FLOOD", "EARTHQUAKE", "CYCLONE", "WILDFIRE"),
                "regions", List.of("NORTH", "SOUTH", "EAST", "WEST", "CENTRAL"),
                "tenureOptions", List.of(1, 2, 3, 5, 7, 10, 15, 20, 30)
        );
        return ResponseEntity.ok(options);
    }
}
