package org.hartford.relief.service.agentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hartford.relief.dto.response.AiPremiumDecision;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.exception.ResourceNotFoundException;
import org.hartford.relief.exception.UnauthorizedAccessException;
import org.hartford.relief.repository.PolicyRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class AiUnderwritingService {

    private final PolicyRepository policyRepository;
    private final ChatClient chatClient;

    public AiUnderwritingService(PolicyRepository policyRepository, ChatClient.Builder chatClientBuilder) {
        this.policyRepository = policyRepository;
        this.chatClient = chatClientBuilder
                .defaultSystem("You are an expert core system underwriter AI for 'Relief' disaster insurance. " +
                        "Examine the physical and logical policy elements to assign a 'suggestedPremium' and 'suggestedCoverage'. " +
                        "Return ONLY a valid JSON string (NO markdown, NO extra text) matching this format: " +
                        "{ \"suggestedCoverage\": number, \"suggestedPremium\": number, \"underwritingReasoning\": \"string\" }")
                .build();
    }

    public AiPremiumDecision calculatePremiumWithAi(Long agentId, Long policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", policyId));

        if (policy.getAgent() == null || !policy.getAgent().getUser().getId().equals(agentId)) {
            throw new UnauthorizedAccessException("Policy does not belong to this agent.");
        }

        try {
            // Build the dynamic risk profile for the text model
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("Customer Policy Profile Data:\n");
            promptBuilder.append("- Disaster Type: ").append(policy.getDisasterType()).append("\n");
            if (policy.getDisasterZone() != null) {
                promptBuilder.append("- Zone Risk Factor (1-10): ").append(policy.getDisasterZone().getRiskFactor()).append("\n");
            }
            promptBuilder.append("- Base Property Value: ₹").append(policy.getPropertyValue()).append("\n");
            promptBuilder.append("- Year Built: ").append(policy.getYearBuilt() != null ? policy.getYearBuilt() : "Unknown").append("\n");
            promptBuilder.append("- Construction Material: ").append(policy.getConstructionMaterial() != null ? policy.getConstructionMaterial() : "Unknown").append("\n");
            promptBuilder.append("- Safety Features: ").append(policy.getSafetyFeatures() != null ? policy.getSafetyFeatures() : "None").append("\n");
            promptBuilder.append("\nRequirements:\n");
            promptBuilder.append("1. Max coverage usually 80-100% of property value (reduce if older than 20 years or poor materials).\n");
            promptBuilder.append("2. Premium should be 0.1% to 1.5% of coverage based strictly on Zone Risk Factor and Material.\n");
            promptBuilder.append("3. Provide exactly 1 highly logical reasoning sentence in 'underwritingReasoning'.\n");

            log.info("[AiUnderwriting] Sending request to Groq API via Spring AI");

            String rawJson = chatClient.prompt()
                    .user(promptBuilder.toString())
                    .call()
                    .content();

            // Clean any potential markdown from Llama 3
            if (rawJson != null) {
                rawJson = rawJson.replace("```json", "").replace("```", "").trim();
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(rawJson, AiPremiumDecision.class);

        } catch (Exception e) {
            log.error("Failed to generate AI premium via Groq", e);
            throw new RuntimeException("Failed to calculate AI premium. Reason: " + e.getMessage());
        }
    }
}