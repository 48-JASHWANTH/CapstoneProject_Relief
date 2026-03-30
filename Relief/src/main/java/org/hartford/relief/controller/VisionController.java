package org.hartford.relief.controller;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.dto.response.VisionAnalysisResult;
import org.hartford.relief.service.VisionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Standalone endpoint to test / invoke Vision AI analysis directly.
 * POST /api/vision/analyze  (multipart/form-data, part name: "file")
 */
@RestController
@RequestMapping("/api/vision")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VisionController {

    private final VisionService visionService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VisionAnalysisResult> analyze(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] bytes = file.getBytes();
            VisionAnalysisResult result = visionService.analyze(bytes);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
