package org.hartford.relief.config;

import lombok.RequiredArgsConstructor;
import org.hartford.relief.entity.*;
import org.hartford.relief.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository         roleRepository;
    private final UserRepository         userRepository;
    private final AgentRepository        agentRepository;
    private final DisasterZoneRepository disasterZoneRepository;
    private final RiskPoolRepository     riskPoolRepository;
    private final PolicyRepository       policyRepository;
    private final ClaimRepository        claimRepository;
    private final PasswordEncoder        passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        // ── ROLES ──────────────────────────────────────────────────
        Role roleAdmin    = saveRole("ADMIN",          "System administrator");
        Role roleAgent    = saveRole("AGENT",          "Underwriter / agent");
        Role roleCustomer = saveRole("CUSTOMER",       "Policyholder");
        Role roleOfficer  = saveRole("CLAIMS_OFFICER", "Claims processing officer");

        // ── USERS ──────────────────────────────────────────────────
        saveUser("Admin User",       "admin@relief.com",    "admin123",    roleAdmin);
        // Agents — one per region
        User john  = saveUser("John Smith",     "john@relief.com",    "agent123",    roleAgent);
        User maria = saveUser("Maria Garcia",   "maria@relief.com",   "agent123",    roleAgent);
        User david = saveUser("David Chen",     "david@relief.com",   "agent123",    roleAgent);
        User emma  = saveUser("Emma Wilson",    "emma@relief.com",    "agent123",    roleAgent);
        User james = saveUser("James Brown",    "james@relief.com",   "agent123",    roleAgent);
        // Customers
        User alice = saveUser("Alice Johnson",  "alice@relief.com",   "customer123", roleCustomer);
        User bob   = saveUser("Bob Williams",   "bob@relief.com",     "customer123", roleCustomer);
        // Claims Officers — one per region
        saveUser("Sara Davis",      "sara@relief.com",    "officer123",  roleOfficer);
        saveUser("Michael Lee",     "michael@relief.com", "officer123",  roleOfficer);
        saveUser("Priya Patel",     "priya@relief.com",   "officer123",  roleOfficer);
        saveUser("Carlos Rodriguez","carlos@relief.com",  "officer123",  roleOfficer);
        saveUser("Lisa Thompson",   "lisa@relief.com",    "officer123",  roleOfficer);

        // ── AGENTS — one per region ────────────────────────────────
        Agent agentJohn  = agentRepository.findByUserId(john.getId()).orElseGet(() ->
                agentRepository.save(Agent.builder()
                        .user(john).licenseNumber("LIC-001").region("NORTH").build()));
        Agent agentMaria = agentRepository.findByUserId(maria.getId()).orElseGet(() ->
                agentRepository.save(Agent.builder()
                        .user(maria).licenseNumber("LIC-002").region("SOUTH").build()));
        Agent agentDavid = agentRepository.findByUserId(david.getId()).orElseGet(() ->
                agentRepository.save(Agent.builder()
                        .user(david).licenseNumber("LIC-003").region("EAST").build()));
        Agent agentEmma  = agentRepository.findByUserId(emma.getId()).orElseGet(() ->
                agentRepository.save(Agent.builder()
                        .user(emma).licenseNumber("LIC-004").region("WEST").build()));
        Agent agentJames = agentRepository.findByUserId(james.getId()).orElseGet(() ->
                agentRepository.save(Agent.builder()
                        .user(james).licenseNumber("LIC-005").region("CENTRAL").build()));

        // ── DISASTER ZONES (4 types × 5 regions = 20 zones) ──────────
        // Risk factor = (disasterTypeScore + regionScore) / 2
        //   Disaster type scores: FLOOD=6, EARTHQUAKE=9, CYCLONE=8, WILDFIRE=5
        //   Region scores:        NORTH=4, SOUTH=7, EAST=5, WEST=4, CENTRAL=6
        // Risk levels: <5.5 → LOW | 5.5-6.9 → MEDIUM | >=7.0 → HIGH

        // NORTH region (regionScore=4)
        DisasterZone northFlood      = saveZone("North Flood Plain",     "NORTH", "LOW",    "FLOOD",      5.0);
        DisasterZone northEarthquake = saveZone("North Seismic Zone",    "NORTH", "MEDIUM", "EARTHQUAKE", 6.5);
        saveZone("North Cyclone Belt",    "NORTH", "MEDIUM", "CYCLONE",    6.0);
        saveZone("North Wildfire Zone",   "NORTH", "LOW",    "WILDFIRE",   4.5);

        // SOUTH region (regionScore=7)
        saveZone("South Flood Plain",     "SOUTH", "MEDIUM", "FLOOD",      6.5);
        saveZone("South Seismic Zone",    "SOUTH", "HIGH",   "EARTHQUAKE", 8.0);
        DisasterZone southCyclone    = saveZone("South Cyclone Belt",    "SOUTH", "HIGH",   "CYCLONE",    7.5);
        saveZone("South Wildfire Zone",   "SOUTH", "MEDIUM", "WILDFIRE",   6.0);

        // EAST region (regionScore=5)
        saveZone("East Flood Plain",      "EAST",  "MEDIUM", "FLOOD",      5.5);
        saveZone("East Seismic Zone",     "EAST",  "HIGH",   "EARTHQUAKE", 7.0);
        saveZone("East Cyclone Belt",     "EAST",  "MEDIUM", "CYCLONE",    6.5);
        saveZone("East Wildfire Zone",    "EAST",  "LOW",    "WILDFIRE",   5.0);

        // WEST region (regionScore=4)
        saveZone("West Flood Plain",      "WEST",  "LOW",    "FLOOD",      5.0);
        saveZone("West Seismic Zone",     "WEST",  "MEDIUM", "EARTHQUAKE", 6.5);
        saveZone("West Cyclone Belt",     "WEST",  "MEDIUM", "CYCLONE",    6.0);
        saveZone("West Wildfire Zone",    "WEST",  "LOW",    "WILDFIRE",   4.5);

        // CENTRAL region (regionScore=6)
        saveZone("Central Flood Plain",     "CENTRAL", "MEDIUM", "FLOOD",      6.0);
        saveZone("Central Seismic Zone",    "CENTRAL", "HIGH",   "EARTHQUAKE", 7.5);
        saveZone("Central Cyclone Belt",    "CENTRAL", "HIGH",   "CYCLONE",    7.0);
        saveZone("Central Wildfire Zone",   "CENTRAL", "MEDIUM", "WILDFIRE",   5.5);

        // ── RISK POOLS ─────────────────────────────────────────────
        RiskPool floodPool    = savePool("FLOOD",      75.0);
        RiskPool eqPool       = savePool("EARTHQUAKE", 70.0);
        RiskPool cyclPool     = savePool("CYCLONE",    80.0);
        savePool("WILDFIRE",   65.0);

        // ── POLICIES ───────────────────────────────────────────────
        savePolicy("POL-ALICE-001", alice, agentJohn, northFlood, floodPool,
                "FLOOD", "STANDARD", "12 Riverside Lane, North Region", "NORTH", 1,
                200000.0, 150000.0, 3900.0, "APPROVED",
                "Approved after underwriting review.",
                LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));

        savePolicy("POL-ALICE-002", alice, agentJohn, northEarthquake, eqPool,
                "EARTHQUAKE", "BASIC", "12 Riverside Lane, North Region", "NORTH", 1,
                200000.0, 100000.0, 1500.0, "PENDING", null,
                LocalDate.of(2026, 2, 1), LocalDate.of(2027, 2, 1));

        Policy bobFlood = savePolicy("POL-BOB-001", bob, agentJohn, northFlood, floodPool,
                "FLOOD", "PREMIUM", "88 Hillcrest Ave, North Region", "NORTH", 1,
                350000.0, 280000.0, 10920.0, "ACTIVE",
                "Premium paid. Policy active.",
                LocalDate.of(2026, 1, 15), LocalDate.of(2027, 1, 15));

        floodPool.setTotalPremiumCollected(10920.0);
        riskPoolRepository.save(floodPool);

        savePolicy("POL-BOB-002", bob, agentJohn, southCyclone, cyclPool,
                "CYCLONE", "STANDARD", "88 Hillcrest Ave, South Region", "SOUTH", 1,
                350000.0, 200000.0, 5600.0, "UNDER_REVIEW",
                "Forwarded for admin approval. Cyclone exposure verified.",
                LocalDate.of(2026, 3, 1), LocalDate.of(2027, 3, 1));

        // ── CLAIM ──────────────────────────────────────────────────
        claimRepository.findByClaimNumber("CLM-BOB-001").orElseGet(() ->
                claimRepository.save(Claim.builder()
                        .claimNumber("CLM-BOB-001")
                        .policy(bobFlood)
                        .riskPool(floodPool)
                        .description("Severe flooding damaged ground floor and basement. Water level reached 4 feet.")
                        .estimatedLoss(75000.0)
                        .status("FILED")
                        .filedDate(LocalDateTime.of(2026, 2, 10, 9, 30))
                        .build()));

        System.out.println("""

╔══════════════════════════════════════════════════════════════════╗
║          RELIEF APP STARTED  —  SEED DATA READY                 ║
╠══════════════════════════════════════════════════════════════════╣
║  Swagger UI  →  
            ║
║  H2 Console  →  http://localhost:8080/h2-console                 ║
║    JDBC URL  :  jdbc:h2:mem:reliefdb                             ║
║    Username  :  root          Password: root                     ║
╠══════════════════════════════════════════════════════════════════╣
║  USERS (email / role)                                            ║
║    admin@relief.com   / ADMIN                                    ║
║    john@relief.com    / AGENT  (NORTH)  LIC-001                  ║
║    maria@relief.com   / AGENT  (SOUTH)  LIC-002                  ║
║    david@relief.com   / AGENT  (EAST)   LIC-003                  ║
║    emma@relief.com    / AGENT  (WEST)   LIC-004                  ║
║    james@relief.com   / AGENT  (CENTRAL)LIC-005                  ║
║    alice@relief.com   / CUSTOMER                                 ║
║    bob@relief.com     / CUSTOMER                                 ║
║    sara@relief.com    / CLAIMS_OFFICER  (NORTH)                  ║
║    michael@relief.com / CLAIMS_OFFICER  (SOUTH)                  ║
║    priya@relief.com   / CLAIMS_OFFICER  (EAST)                   ║
║    carlos@relief.com  / CLAIMS_OFFICER  (WEST)                   ║
║    lisa@relief.com    / CLAIMS_OFFICER  (CENTRAL)                ║
╠══════════════════════════════════════════════════════════════════╣
║  POLICIES                                                        ║
║    policyId=1  Alice  FLOOD       APPROVED   <- pay premium      ║
║    policyId=2  Alice  EARTHQUAKE  PENDING    <- agent review     ║
║    policyId=3  Bob    FLOOD       ACTIVE     <- file claim       ║
║    policyId=4  Bob    CYCLONE     UNDER_REVIEW <- admin approve  ║
╠══════════════════════════════════════════════════════════════════╣
║  CLAIMS                                                          ║
║    claimId=1  Bob/POL-BOB-001  FILED -> officer review           ║
╚══════════════════════════════════════════════════════════════════╝
""");
    }

    private Role saveRole(String name, String description) {
        return roleRepository.findByName(name).orElseGet(() ->
                roleRepository.save(Role.builder().name(name).description(description).build()));
    }

    private User saveUser(String name, String email, String password, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User u = User.builder()
                    .name(name).email(email).password(passwordEncoder.encode(password))
                    .status("ACTIVE").createdAt(LocalDateTime.now())
                    .role(role)
                    .build();
            return userRepository.save(u);
        });
    }

    private DisasterZone saveZone(String zoneName, String location, String riskLevel, String type, double riskFactor) {
        return disasterZoneRepository.findByZoneName(zoneName).orElseGet(() ->
                disasterZoneRepository.save(DisasterZone.builder()
                        .zoneName(zoneName).location(location)
                        .riskLevel(riskLevel).disasterType(type)
                        .riskFactor(riskFactor).build()));
    }

    private RiskPool savePool(String disasterType, double threshold) {
        return riskPoolRepository.findByDisasterType(disasterType).orElseGet(() ->
                riskPoolRepository.save(RiskPool.builder()
                        .disasterType(disasterType)
                        .totalPremiumCollected(0.0).totalClaimsPaid(0.0)
                        .thresholdPercentage(threshold).poolStatus("ACTIVE").build()));
    }

    private Policy savePolicy(String number, User user, Agent agent,
                               DisasterZone zone, RiskPool pool,
                               String disasterType, String policyType, String address, String region, int tenure,
                               double propValue, double sumInsured, double premium,
                               String status, String remarks, LocalDate start, LocalDate end) {
        return policyRepository.findByPolicyNumber(number).orElseGet(() ->
                policyRepository.save(Policy.builder()
                        .policyNumber(number).user(user).agent(agent)
                        .disasterZone(zone).riskPool(pool)
                        .disasterType(disasterType).policyType(policyType)
                        .propertyAddress(address).propertyValue(propValue)
                        .region(region).tenure(tenure)
                        .sumInsured(sumInsured).premiumAmount(premium)
                        .status(status).remarks(remarks)
                        .startDate(start).endDate(end).build()));
    }
}
