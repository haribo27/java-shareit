package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ErrorResponse handleConstraintValidationException(final Exception e) {
        log.info("Status code 400 {}", e.getMessage());
        return new ErrorResponse(
                "Check the correctness of the input data, status code 400");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleNotUniqueFieldException(final NotUniqueDataException e) {
        log.info("Field must be unique {}", e.getMessage());
        return new ErrorResponse("Email must be unique field " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class, IncorrectArgumentException.class})
    public ErrorResponse handleEntityNotFound(final Exception e) {
        log.info("Entity not found 404 {}", e.getMessage());
        return new ErrorResponse("Entity not found status 404 " + e.getMessage());
    }
}
