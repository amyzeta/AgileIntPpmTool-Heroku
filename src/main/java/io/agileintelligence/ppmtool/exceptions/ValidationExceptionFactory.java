package io.agileintelligence.ppmtool.exceptions;

public class ValidationExceptionFactory {
    public static ValidationException forProjectIdentifier(final String message) {
        return new ValidationException("projectIdentifier", message);
    }

    public static ValidationException forId(final String message) {
        return new ValidationException("id", message);
    }

}
