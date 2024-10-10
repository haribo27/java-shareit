package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

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
                "Entity not found"),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNotEnoughRightsToChangeBooking(final NotEnoughRightsToChangeData e) {
        log.info("Status code: 400 {}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse("403 " + e.getMessage(),
                "Wrong booking owner"),
                HttpStatus.BAD_REQUEST);
    }
}
