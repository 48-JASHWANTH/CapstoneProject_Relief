package org.hartford.relief.exception;

/**
 * Thrown when a user tries to access or modify a resource that does not belong to them.
 * Maps to HTTP 403 Forbidden.
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String resource, Long userId) {
        super(resource + " does not belong to user with id: " + userId);
    }
}
