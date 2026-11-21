package com.ironhack.springbootrestapi.exception;

/**
 * The "API-Key" header was sent but does not hold the expected value.
 * Mapped to 401 Unauthorized. A missing header is reported separately by
 * Spring as {@code MissingRequestHeaderException}.
 */
public class InvalidApiKeyException extends RuntimeException {

    public InvalidApiKeyException() {
        super("The API-Key header is invalid.");
    }
}
