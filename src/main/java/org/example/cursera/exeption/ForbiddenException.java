package org.example.cursera.exeption;

import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends AbstractException {
    public ForbiddenException(ErrorDto errorDto){
        super(errorDto);
    }

    public ForbiddenException(List<ErrorDto> errors) {
        super(errors);
    }

    public ForbiddenException() {
    }
}
