package io.agileintelligence.ppmtool.validator;

import io.agileintelligence.ppmtool.dto.SignupDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SignupValidator implements Validator {
    @Override
    public boolean supports(final Class<?> clazz) {
        return SignupDto.class.equals(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final SignupDto signup = (SignupDto)target;
        if (signup.getPassword().length() < 6) {
            errors.rejectValue("password", "Length", "password must be at least 6 characters");
        }
        if (!signup.getPassword().equals(signup.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Passwords must match");
        }
    }
}
