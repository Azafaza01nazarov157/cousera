package org.example.cursera.domain.dtos.errors;



import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class ErrorResponse {
    @NotEmpty
    private final List<ErrorDto> errors;

    public static ErrorResponse of(@Valid @NotNull ErrorDto error) {
        return new ErrorResponse(List.of(error));
    }

    public static ErrorResponse ofThrowable(@NotNull Throwable t) {
        return ofThrowable("0500", t);
    }

    public static ErrorResponse ofThrowable(@NotBlank String code, @NotNull Throwable t) {
        return new ErrorResponse(List.of(new ErrorDto(code, t.getMessage())));
    }

    public static ErrorResponse ofErrors(@NotEmpty List<ErrorDto> errors) {
        return new ErrorResponse(errors);
    }

    public List<ErrorDto> getErrors() {
        return this.errors;
    }

    public ErrorResponse(final List<ErrorDto> errors) {
        this.errors = errors;
    }
}
