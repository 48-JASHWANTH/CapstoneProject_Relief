package org.hartford.relief.ai;

import org.hartford.relief.entity.Agent;
import org.hartford.relief.entity.Claim;
import org.hartford.relief.entity.Policy;
import org.hartford.relief.entity.User;
import org.hartford.relief.repository.AgentRepository;
import org.hartford.relief.repository.ClaimRepository;
import org.hartford.relief.repository.PolicyRepository;
import org.hartford.relief.repository.UserRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ChatService(ChatClient.Builder chatClientBuilder,
                       PolicyRepository policyRepository,
                       ClaimRepository claimRepository,
                       UserRepository userRepository,
                       AgentRepository agentRepository) {
        this.chatClient = chatClientBuilder.build();
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
    }

    public String chat(String conversationId, String message, Long userId, String userRole) {
        String systemPrompt = buildSystemPrompt(userId, userRole);
        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .content();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // System prompt builder — dispatches per role
    // ──────────────────────────────────────────────────────────────────────────

    private String buildSystemPrompt(Long userId, String userRole) {
        if (userId == null || userRole == null) {
            return buildGuestPrompt();
        }
        return switch (userRole.toUpperCase()) {
            case "CUSTOMER" -> buildCustomerPrompt(userId);
            case "AGENT"    -> buildAgentPrompt(userId);
            case "CLAIMS_OFFICER" -> buildClaimsOfficerPrompt(userId);
            case "ADMIN"    -> buildAdminPrompt();
            default         -> buildGuestPrompt();
        };
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GUEST
    // ──────────────────────────────────────────────────────────────────────────
    private String buildGuestPrompt() {
        return """
                You are the Relief AI Assistant — a friendly, empathetic helper for the Relief Disaster Insurance Management System.
                
                Relief offers insurance policies for: Flood, Earthquake, Cyclone, and Hurricane disasters.
                
                As a guest, help visitors understand:
                - What disaster insurance types Relief offers
                - How to register as a customer
                - General information about claim filing processes
                - How Relief protects communities during disasters
                
                Be concise, warm, and encouraging. Guide them toward registering if they need coverage.
                Do NOT invent specific policy details, prices, or claim numbers.
                Always suggest the user to log in or register for personalised assistance.
                """;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // CUSTOMER
    // ──────────────────────────────────────────────────────────────────────────
    private String buildCustomerPrompt(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        String customerName = userOpt.map(User::getName).orElse("Customer");

        List<Policy> policies = policyRepository.findByUserId(userId);
        List<Claim>  claims   = claimRepository.findByPolicy_UserId(userId);

        StringBuilder sb = new StringBuilder();
        sb.append("You are the Relief AI Assistant speaking with ").append(customerName).append(".\n\n");
        sb.append("Be warm, empathetic, and concise. Address them by first name when appropriate.\n\n");

        // Policies context
        sb.append("=== ").append(customerName).append("'s POLICIES ===\n");
        if (policies.isEmpty()) {
            sb.append("This customer currently has NO active policies.\n");
        } else {
            sb.append("Total policies: ").append(policies.size()).append("\n");
            for (Policy p : policies) {
                sb.append("- Policy #").append(p.getPolicyNumber())
                  .append(" | Type: ").append(p.getDisasterType())
                  .append(" | Status: ").append(p.getStatus())
                  .append(" | Premium: $").append(String.format("%.2f", p.getPremiumAmount() != null ? p.getPremiumAmount() : 0.0))
                  .append("/month")
                  .append(" | Sum Insured: $").append(String.format("%.2f", p.getSumInsured() != null ? p.getSumInsured() : 0.0))
                  .append(" | Coverage until: ").append(p.getEndDate() != null ? p.getEndDate().format(DATE_FMT) : "N/A")
                  .append(" | Next Due: ").append(p.getNextPremiumDueDate() != null ? p.getNextPremiumDueDate().format(DATE_FMT) : "N/A")
                  .append("\n");
            }
        }

        // Claims context
        sb.append("\n=== ").append(customerName).append("'s CLAIMS ===\n");
        if (claims.isEmpty()) {
            sb.append("This customer has NOT filed any claims.\n");
        } else {
            sb.append("Total claims: ").append(claims.size()).append("\n");
            for (Claim c : claims) {
                sb.append("- Claim #").append(c.getClaimNumber())
                  .append(" | Status: ").append(c.getStatus())
                  .append(" | Damage: ").append(c.getDamageType())
                  .append(" | Estimated Loss: $").append(String.format("%.2f", c.getEstimatedLoss() != null ? c.getEstimatedLoss() : 0.0))
                  .append(" | Filed: ").append(c.getFiledDate() != null ? c.getFiledDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A")
                  .append(" | Approved Amount: ").append(c.getApprovedAmount() != null ? "$" + String.format("%.2f", c.getApprovedAmount()) : "Pending")
                  .append("\n");
            }
        }

        sb.append("""
                
                === YOUR ROLE ===
                Answer ONLY about this customer's own policies and claims using the data above.
                You can help with:
                - Policy status, premium details, due dates, coverage info
                - Claim status, approved amounts, what to expect
                - How to file a new claim (go to the Claims section in the dashboard)
                - How to make premium payments
                - What documents are needed for claims
                Do NOT reveal other customers' information. Be empathetic if a claim is pending.
                """);

        return sb.toString();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // AGENT
    // ──────────────────────────────────────────────────────────────────────────
    private String buildAgentPrompt(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        String agentName = userOpt.map(User::getName).orElse("Agent");

        Optional<Agent> agentOpt = agentRepository.findByUserId(userId);
        Long agentId = agentOpt.map(Agent::getId).orElse(null);

        List<Policy> assignedPolicies = agentId != null
                ? policyRepository.findByAgentId(agentId)
                : List.of();

        long pendingCount  = assignedPolicies.stream().filter(p -> "PENDING".equalsIgnoreCase(p.getStatus())).count();
        long activeCount   = assignedPolicies.stream().filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus())).count();
        long rejectedCount = assignedPolicies.stream().filter(p -> "REJECTED".equalsIgnoreCase(p.getStatus())).count();

        List<Claim> agentClaims = agentId != null
                ? claimRepository.findByPolicy_AgentId(agentId)
                : List.of();

        long claimsPending  = agentClaims.stream().filter(c -> "PENDING".equalsIgnoreCase(c.getStatus())).count();
        long claimsApproved = agentClaims.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count();

        StringBuilder sb = new StringBuilder();
        sb.append("You are the Relief AI Assistant speaking with Agent ").append(agentName).append(".\n\n");

        sb.append("=== AGENT PORTFOLIO SUMMARY ===\n");
        sb.append("Total assigned policies: ").append(assignedPolicies.size()).append("\n");
        sb.append("- Active: ").append(activeCount).append("\n");
        sb.append("- Pending approval: ").append(pendingCount).append("\n");
        sb.append("- Rejected: ").append(rejectedCount).append("\n\n");

        sb.append("=== ASSOCIATED CLAIMS ===\n");
        sb.append("Total claims on agent's policies: ").append(agentClaims.size()).append("\n");
        sb.append("- Pending: ").append(claimsPending).append("\n");
        sb.append("- Approved: ").append(claimsApproved).append("\n\n");

        if (!assignedPolicies.isEmpty()) {
            sb.append("=== POLICY LIST (recent 10) ===\n");
            assignedPolicies.stream().limit(10).forEach(p ->
                sb.append("- Policy #").append(p.getPolicyNumber())
                  .append(" | ").append(p.getDisasterType())
                  .append(" | Status: ").append(p.getStatus())
                  .append(" | Customer: ").append(p.getUser() != null ? p.getUser().getName() : "N/A")
                  .append("\n")
            );
        }

        sb.append("""
                
                === YOUR ROLE ===
                Help this agent with:
                - Overview of their policy portfolio
                - Pending policies that need customer documents
                - How to adjust premiums or use the AI prediction tool
                - Understanding claim statuses on their policies
                - How to guide customers through policy activation steps
                - Best practices for disaster insurance underwriting
                Be professional and efficient. Use bullet points for clarity.
                """);

        return sb.toString();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // CLAIMS OFFICER
    // ──────────────────────────────────────────────────────────────────────────
    private String buildClaimsOfficerPrompt(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        String officerName = userOpt.map(User::getName).orElse("Officer");

        List<Claim> assignedClaims = claimRepository.findByAssignedOfficer_Id(userId);
        List<Claim> unassignedClaims = claimRepository.findByAssignedOfficerIsNull();

        long pending  = assignedClaims.stream().filter(c -> "PENDING".equalsIgnoreCase(c.getStatus())).count();
        long approved = assignedClaims.stream().filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus())).count();
        long rejected = assignedClaims.stream().filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus())).count();

        StringBuilder sb = new StringBuilder();
        sb.append("You are the Relief AI Assistant speaking with Claims Officer ").append(officerName).append(".\n\n");

        sb.append("=== CLAIMS OFFICER DASHBOARD ===\n");
        sb.append("Claims assigned to you: ").append(assignedClaims.size()).append("\n");
        sb.append("- Pending review: ").append(pending).append("\n");
        sb.append("- Approved: ").append(approved).append("\n");
        sb.append("- Rejected: ").append(rejected).append("\n");
        sb.append("Unassigned claims in system: ").append(unassignedClaims.size()).append("\n\n");

        if (!assignedClaims.isEmpty()) {
            sb.append("=== YOUR ASSIGNED CLAIMS (recent 10) ===\n");
            assignedClaims.stream().limit(10).forEach(c ->
                sb.append("- Claim #").append(c.getClaimNumber())
                  .append(" | Status: ").append(c.getStatus())
                  .append(" | Damage: ").append(c.getDamageType())
                  .append(" | Loss: $").append(String.format("%.2f", c.getEstimatedLoss() != null ? c.getEstimatedLoss() : 0.0))
                  .append(" | Filed: ").append(c.getFiledDate() != null ? c.getFiledDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A")
                  .append(" | Policy: ").append(c.getPolicy() != null ? c.getPolicy().getPolicyNumber() : "N/A")
                  .append("\n")
            );
        }

        sb.append("""
                
                === YOUR ROLE ===
                Help this claims officer with:
                - Status overview of their assigned claims caseload
                - How to review damage documents and AI analysis results
                - Guidelines for approving or rejecting claims
                - How to add officer remarks
                - Escalation procedures for high-value claims
                - Understanding AI-generated damage severity scores
                Be precise and professional. You understand insurance claims processing deeply.
                """);

        return sb.toString();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // ADMIN
    // ──────────────────────────────────────────────────────────────────────────
    private String buildAdminPrompt() {
        long totalPolicies = policyRepository.count();
        long totalClaims   = claimRepository.count();

        long activePolicies  = policyRepository.findByStatus("ACTIVE").size();
        long pendingPolicies = policyRepository.findByStatus("PENDING").size();
        long pendingClaims   = claimRepository.findByStatus("PENDING").size();
        long approvedClaims  = claimRepository.findByStatus("APPROVED").size();
        long unassigned      = claimRepository.findByAssignedOfficerIsNull().size();

        return """
                You are the Relief AI Assistant speaking with a System Administrator.
                
                === SYSTEM-WIDE OVERVIEW ===
                Total Policies: """ + totalPolicies + """
                
                - Active: """ + activePolicies + """
                
                - Pending: """ + pendingPolicies + """
                
                Total Claims: """ + totalClaims + """
                
                - Pending Review: """ + pendingClaims + """
                
                - Approved: """ + approvedClaims + """
                
                - Unassigned Claims: """ + unassigned + """
                
                
                === YOUR ROLE ===
                Help the admin with:
                - System-wide statistics and health overview
                - Managing users (customers, agents, claims officers)
                - Reviewing and activating pending policies
                - Assigning claims officers to unassigned claims
                - Understanding risk pool management
                - Disaster zone configuration
                - Generating reports and analytics insights
                Be authoritative, data-driven, and precise.
                """;
    }
}
