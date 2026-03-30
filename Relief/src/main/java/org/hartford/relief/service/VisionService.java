package org.hartford.relief.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.hartford.relief.dto.response.VisionAnalysisResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class VisionService {

    @Value("${gcp.credentials.location}")
    private String credentialsPath;

    // ---------------------------------------------------------------
    // Label → damage category mapping
    // ---------------------------------------------------------------
    private static final Map<String, List<String>> DAMAGE_KEYWORDS = Map.of(
        "Flood",      List.of("flood", "water", "submerged", "inundation", "waterlogged", "rain", "storm surge"),
        "Structural", List.of("crack", "collapse", "rubble", "debris", "broken wall", "structural", "ruin", "destroyed"),
        "Fire",       List.of("fire", "burn", "smoke", "ash", "flame", "charred", "scorch"),
        "Wind",       List.of("wind", "roof", "torn", "cyclone", "hurricane", "uprooted", "fallen tree")
    );

    // ---------------------------------------------------------------
    // Suggested loss ranges (severity × damage type)
    // ---------------------------------------------------------------
    private static final Map<String, Map<String, String>> LOSS_TABLE = Map.of(
        "Flood", Map.of(
            "Minor",    "₹25,000 – ₹75,000",
            "Moderate", "₹75,000 – ₹2,00,000",
            "Severe",   "₹2,00,000 – ₹5,00,000"
        ),
        "Structural", Map.of(
            "Minor",    "₹50,000 – ₹1,50,000",
            "Moderate", "₹1,50,000 – ₹4,00,000",
            "Severe",   "₹4,00,000 – ₹10,00,000"
        ),
        "Fire", Map.of(
            "Minor",    "₹30,000 – ₹1,00,000",
            "Moderate", "₹1,00,000 – ₹3,00,000",
            "Severe",   "₹3,00,000 – ₹8,00,000"
        ),
        "Wind", Map.of(
            "Minor",    "₹20,000 – ₹60,000",
            "Moderate", "₹60,000 – ₹1,50,000",
            "Severe",   "₹1,50,000 – ₹4,00,000"
        ),
        "Unknown", Map.of(
            "Minor",    "₹10,000 – ₹50,000",
            "Moderate", "₹50,000 – ₹1,50,000",
            "Severe",   "₹1,50,000 – ₹5,00,000"
        )
    );

    /**
     * Analyse an image's bytes using Google Cloud Vision Label Detection.
     *
     * @param imageBytes raw bytes of the uploaded image
     * @return VisionAnalysisResult with damageType, severity, confidence, suggestedLoss
     */
    public VisionAnalysisResult analyze(byte[] imageBytes) {
        try (ImageAnnotatorClient client = buildClient()) {

            // Build Vision API request
            ByteString imgBytes = ByteString.copyFrom(imageBytes);
            Image image = Image.newBuilder().setContent(imgBytes).build();
            Feature feature = Feature.newBuilder()
                    .setType(Feature.Type.LABEL_DETECTION)
                    .setMaxResults(15)
                    .build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(image)
                    .build();

            BatchAnnotateImagesResponse batchResponse =
                    client.batchAnnotateImages(List.of(request));

            List<EntityAnnotation> labels =
                    batchResponse.getResponses(0).getLabelAnnotationsList();

            if (labels.isEmpty()) {
                return buildResult("Unknown", "Minor", 0.0);
            }

            // Best label confidence (converted from [0,1] to [0,100])
            double topConfidence = labels.get(0).getScore() * 100.0;

            // Determine damage type by scanning all label descriptions
            String damageType = classifyDamage(labels);

            // Determine severity based on top confidence score
            String severity = classifySeverity(topConfidence);

            log.info("[VisionService] Labels: {}, DamageType: {}, Severity: {}, Confidence: {}%",
                    labels.stream().map(EntityAnnotation::getDescription).toList(),
                    damageType, severity, String.format("%.1f", topConfidence));

            return buildResult(damageType, severity, Math.round(topConfidence * 10.0) / 10.0);

        } catch (Exception e) {
            log.error("[VisionService] Analysis failed: {}", e.getMessage(), e);
            // Return a safe fallback instead of throwing
            return buildResult("Unknown", "Minor", 0.0);
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private ImageAnnotatorClient buildClient() throws IOException {
        GoogleCredentials credentials =
                GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                        .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();
        return ImageAnnotatorClient.create(settings);
    }

    private String classifyDamage(List<EntityAnnotation> labels) {
        List<String> descriptions = labels.stream()
                .map(l -> l.getDescription().toLowerCase())
                .toList();

        for (Map.Entry<String, List<String>> entry : DAMAGE_KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (descriptions.stream().anyMatch(d -> d.contains(keyword))) {
                    return entry.getKey();
                }
            }
        }
        return "Unknown";
    }

    private String classifySeverity(double confidence) {
        if (confidence >= 80.0) return "Severe";
        if (confidence >= 60.0) return "Moderate";
        return "Minor";
    }

    private VisionAnalysisResult buildResult(String damageType, String severity, double confidence) {
        String suggestedLoss = LOSS_TABLE
                .getOrDefault(damageType, LOSS_TABLE.get("Unknown"))
                .getOrDefault(severity, "₹10,000 – ₹50,000");
        return VisionAnalysisResult.builder()
                .damageType(damageType)
                .severity(severity)
                .confidence(confidence)
                .suggestedLoss(suggestedLoss)
                .build();
    }
}
