package org.example.cursera.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.dtos.errors.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final Function<ConstraintViolation<?>, String> M = s -> String.format(s.getMessageTemplate(), s.getInvalidValue());

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ConstraintViolationException ex, WebRequest request) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(M)
                .collect(Collectors.joining());

        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(message);

        return response(BAD_REQUEST, ErrorResponse.of(errorDto));
    }


    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessing(JsonProcessingException ex) {
        return response(BAD_REQUEST, ErrorResponse.of(ErrorDto.builder()
                .code(BAD_REQUEST.toString())
                .message("Json parsing error: %s".formatted(Stream.of(Objects.requireNonNull(ex.getMessage()))
                        .collect(Collectors.joining()))).build()));
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, ErrorResponse response) {
        return ResponseEntity.status(status)
                .headers(createHeaders())
                .body(response);
    }

    private static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}