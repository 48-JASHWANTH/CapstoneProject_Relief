package org.hartford.relief.controller.userController;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.repository.RiskPoolRepository;
import org.hartford.relief.entity.RiskPool;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/policy-options")
@RequiredArgsConstructor
public class PolicyOptionsController {

    private final RiskPoolRepository riskPoolRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPolicyOptions() {
        List<String> disasterTypes = riskPoolRepository.findAll()
                .stream()
                .map(RiskPool::getDisasterType)
                .filter(t -> t != null && !t.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (disasterTypes.isEmpty()) {
            disasterTypes = List.of("FLOOD", "EARTHQUAKE", "CYCLONE", "WILDFIRE");
        }

        Map<String, Object> options = Map.of(
                "disasterTypes", disasterTypes,
                "regions", List.of("NORTH", "SOUTH", "EAST", "WEST", "CENTRAL"),
                "tenureOptions", List.of(1, 2, 3, 5, 7, 10, 15, 20, 30)
        );
        return ResponseEntity.ok(options);
    }
}
