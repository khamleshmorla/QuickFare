package com.quickfare.exception;

/**
 * Thrown when an external API call fails (Google Maps, OpenCage, etc.).
 */
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
