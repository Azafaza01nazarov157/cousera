package org.example.cursera.exeption;


import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.List;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends AbstractException {
    public UnauthorizedException(ErrorDto error) {
        super(error);
    }

    public UnauthorizedException(List<ErrorDto> errors) {
        super(errors);
    }

    public UnauthorizedException() {
    }
}
