package org.hartford.relief.exception;

/**
 * Thrown when an operation requires a user to hold a particular role that they do not have.
 * For example, assigning a policy to a user who is not a CLAIMS_OFFICER.
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String message) {
        super(message);
    }

    public InvalidRoleException(Long userId, String requiredRole) {
        super("User id: " + userId + " does not have the required role: " + requiredRole + ".");
    }
}
