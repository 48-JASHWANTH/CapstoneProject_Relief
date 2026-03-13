package org.hartford.relief.exception;

/**
 * Thrown when a required system configuration is missing or invalid at runtime.
 * For example, the default CUSTOMER role not existing in the database.
 * Maps to HTTP 500 Internal Server Error.
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String component, String detail) {
        super("System configuration error in [" + component + "]: " + detail);
    }
}
