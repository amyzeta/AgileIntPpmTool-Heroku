package io.agileintelligence.ppmtool.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public final Map<String, String> handleValidationException(final ValidationException exception) {
        return Collections.singletonMap(exception.getField(), exception.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        // spring's default handling of HttpMessageNotReadableException puts nothing at all in the body which slows me down realising what went wrong
        return new ResponseEntity<>("An error occurred parsing the request body. See server log for details.", HttpStatus.BAD_REQUEST);
    }

}
