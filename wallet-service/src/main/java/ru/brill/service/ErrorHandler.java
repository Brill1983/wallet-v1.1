package ru.brill.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.brill.exceptions.BadParameterException;
import ru.brill.exceptions.ElementNotFoundException;
import ru.brill.exceptions.RestrictedOperationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExistExc(DataIntegrityViolationException e) {
        log.info("Validation: {}", e.getMessage());
        return new ErrorResponse("Пользователь с такой почтой уже зарегистрирован");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadParameterExc(BadParameterException e) {
        log.info("Validation: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExc(ElementNotFoundException e) {
        log.info("Element not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleRestrictedOperationExc(RestrictedOperationException e) {
        log.info("Restricted operation: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleRestrictedOperationExc(SQLException e) {
        log.info("Restricted operation: {}", e.getMessage());
        return new ErrorResponse("Транзакция отменена, произошли стороннее изменения в процессе исполнения." +
                "Попробуйте выполнить транзакцию снова.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExc(Throwable e) {
        log.info("Error: ", e);
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return new ErrorResponse(errors.toString());
    }
}
