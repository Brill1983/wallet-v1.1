package ru.brill.exceptions;

public class BadParameterException extends RuntimeException { // TODO - возможно можно удалить
    public BadParameterException(String message) {
        super(message);
    }
}
