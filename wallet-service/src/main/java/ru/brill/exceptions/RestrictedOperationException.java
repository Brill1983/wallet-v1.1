package ru.brill.exceptions;

public class RestrictedOperationException extends RuntimeException {
    public RestrictedOperationException(String message) {
        super(message);
    }
}
