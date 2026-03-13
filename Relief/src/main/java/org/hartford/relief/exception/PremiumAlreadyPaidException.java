package org.hartford.relief.exception;

/**
 * Thrown when a customer tries to pay the premium for a policy that has already been paid.
 * Maps to HTTP 409 Conflict.
 */
public class PremiumAlreadyPaidException extends RuntimeException {

    public PremiumAlreadyPaidException(String message) {
        super(message);
    }

    public PremiumAlreadyPaidException(Long policyId) {
        super("Premium has already been paid for policy id: " + policyId + ".");
    }
}
