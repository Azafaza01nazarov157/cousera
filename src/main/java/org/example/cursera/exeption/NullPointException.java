package org.example.cursera.exeption;

import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NullPointException extends AbstractException {
    public NullPointException(ErrorDto errorDto){
        super(errorDto);
    }

    public NullPointException(List<ErrorDto> errors) {
        super(errors);
    }

    public NullPointException() {
    }
}
