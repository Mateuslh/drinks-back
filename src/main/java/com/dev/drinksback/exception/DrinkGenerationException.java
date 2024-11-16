package com.dev.drinksback.exception;


public class DrinkGenerationException extends RuntimeException {
    public DrinkGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DrinkGenerationException(String message) {
        super(message);
    }
}