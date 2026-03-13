package org.hartford.relief.exception;

/**
 * Thrown when a monetary value fails validation:
 * - amount is zero or negative
 * - amount exceeds the policy's sum insured
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(String message) {
        super(message);
    }

    /**
     * Use when a provided amount exceeds an allowed maximum.
     *
     * @param field    descriptive name of the amount field (e.g. "Estimated loss", "Approved amount")
     * @param provided the value the caller supplied
     * @param maximum  the maximum value that is allowed
     */
    public InvalidAmountException(String field, double provided, double maximum) {
        super(field + " (" + provided + ") cannot exceed the maximum allowed value (" + maximum + ").");
    }
}
