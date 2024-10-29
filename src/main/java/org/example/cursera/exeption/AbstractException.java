package org.example.cursera.exeption;



import org.example.cursera.domain.dtos.errors.ErrorDto;

import java.util.List;

public abstract class AbstractException extends RuntimeException {

    protected List<ErrorDto> errors;

    public AbstractException(ErrorDto error) {
        super(error.getMessage());
        this.errors = List.of(error);
    }

    public AbstractException(ErrorDto error, Throwable cause) {
        super(error.getMessage(), cause);
        this.errors = List.of(error);
    }

    public AbstractException(List<ErrorDto> errors) {
        super(errors.toString());
        this.errors = errors;
    }

    public List<ErrorDto> getErrors() {
        return this.errors;
    }

    public void setErrors(final List<ErrorDto> errors) {
        this.errors = errors;
    }

    public AbstractException() {
    }
}
