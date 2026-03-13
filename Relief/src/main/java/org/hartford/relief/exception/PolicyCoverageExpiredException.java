package org.hartford.relief.exception;

/**
 * Thrown when a customer tries to file a claim on a policy that is outside
 * its active coverage period (startDate/endDate window).
 * Maps to HTTP 400 Bad Request.
 */
public class PolicyCoverageExpiredException extends RuntimeException {

    public PolicyCoverageExpiredException(String message) {
        super(message);
    }

    public PolicyCoverageExpiredException(Long policyId) {
        super("Policy id: " + policyId + " is not within its active coverage period."
                + " Claims may only be filed within the policy's start and end dates.");
    }
}
