package com.ironhack.springbootrestapi.exception;

/**
 * No customer has the requested email. Mapped to 404 Not Found.
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String email) {
        super("Customer with email '%s' was not found.".formatted(email));
    }
}
