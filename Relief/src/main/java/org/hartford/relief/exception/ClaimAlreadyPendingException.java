package org.hartford.relief.exception;

/**
 * Thrown when a claim is already in PENDING / UNDER_REVIEW state for a given policy,
 * preventing the user from filing a duplicate claim until the existing one is resolved.
 * Maps to HTTP 409 Conflict.
 */
public class ClaimAlreadyPendingException extends RuntimeException {

    public ClaimAlreadyPendingException(String message) {
        super(message);
    }

    public ClaimAlreadyPendingException(Long policyId) {
        super("A claim is already pending review for policy id: " + policyId
                + ". Please wait for it to be resolved before filing another.");
    }
}
