package com.ironhack.springbootrestapi.exception;

/**
 * The {@code min}/{@code max} pair of a price-range search is not a usable
 * range, for example a negative bound or {@code min} greater than {@code max}.
 * Mapped to 400 Bad Request.
 */
public class InvalidPriceRangeException extends RuntimeException {

    public InvalidPriceRangeException(String message) {
        super(message);
    }
}
