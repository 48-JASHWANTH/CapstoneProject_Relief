package org.hartford.relief.dto.request;

import lombok.Data;

@Data
public class CreateUserRequest {

    private String name;
    private String email;
    private String password;

    /** Role name to assign, e.g. "AGENT", "CUSTOMER", "CLAIMS_OFFICER" */
    private String roleName;

    // ── Agent-specific fields (required when roleName is "AGENT") ──
    private String licenseNumber;
    private String region;
}
