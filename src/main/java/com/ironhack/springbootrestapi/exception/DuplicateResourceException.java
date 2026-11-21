package com.ironhack.springbootrestapi.exception;

/**
 * A create or update would give two products the same name, or two customers
 * the same email. Mapped to 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
