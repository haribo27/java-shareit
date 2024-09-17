package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class, ItemIsNotAvailable.class})
    public ResponseEntity<?> handleConstraintValidationException(final Exception e) {
        log.info("Status code 400 {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("400 " + e.getMessage(),
                "Incorrect input data"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNotUniqueFieldException(final EmailAlreadyExist e) {
        log.info("Email already exist {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("409 " + e.getMessage(),
                "Email already exist"),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({EntityNotFoundException.class, IncorrectArgumentException.class})
    public ResponseEntity<?> handleEntityNotFound(final Exception e) {
        log.info("Entity not found 404 {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("404 " + e.getMessage(),
                "Entity not founf"),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleOtherExceptions(final Throwable e) {
        log.info("Status code: 500 {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("500 " + e.getMessage(),
                "Internal Server error"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNotEnoughRightsToChangeBooking(final NotEnoughRightsToChangeBooking e) {
        log.info("Status code: 400 {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("403 " + e.getMessage(),
                "Wrong booking owner"),
                HttpStatus.BAD_REQUEST);
    }
}
