package com.ironhack.springbootrestapi.exception;

/**
 * No product in the catalogue has the requested name. Mapped to 404 Not Found.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String name) {
        super("Product '%s' was not found.".formatted(name));
    }
}
