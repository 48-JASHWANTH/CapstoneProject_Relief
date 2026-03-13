package org.hartford.relief.exception;

/**
 * Thrown when a status transition is not allowed for an entity
 * (e.g. approving a claim that is already PAID, or moving a non-FILED claim to UNDER_REVIEW).
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(String entity, String currentStatus, String targetStatus) {
        super(entity + " cannot transition from '" + currentStatus + "' to '" + targetStatus + "'.");
    }
}
