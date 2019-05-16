package io.agileintelligence.ppmtool.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ValidationErrorService {

    public Optional<ResponseEntity<?>> validationErrorMessage(final BindingResult result) {
        if (result.hasErrors()) {
            //noinspection ConstantConditions
            final Map<String, String> errorMap = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (s1, s2) -> s1));
            return Optional.of(new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST));
        } else {
            return Optional.empty();
        }
    }

}
