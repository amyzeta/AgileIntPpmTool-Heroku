package io.agileintelligence.ppmtool.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private final String field;

    String getField() {
        return field;
    }

    public ValidationException(final String field, final String message) {
        super(message);
        this.field = field;
    }
}
