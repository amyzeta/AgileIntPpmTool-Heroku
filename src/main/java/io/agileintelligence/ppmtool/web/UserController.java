package io.agileintelligence.ppmtool.web;

import io.agileintelligence.ppmtool.dto.SignupDto;
import io.agileintelligence.ppmtool.services.UserService;
import io.agileintelligence.ppmtool.services.ValidationErrorService;
import io.agileintelligence.ppmtool.validator.SignupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private ValidationErrorService validationErrorService;

    @Autowired
    private UserService userService;

    @Autowired
    private SignupValidator signupValidator;

    @PostMapping("/register")
    public ResponseEntity<?> register(final @Valid @RequestBody SignupDto user, final BindingResult result) {
        signupValidator.validate(user, result);
        final Optional<ResponseEntity<?>> responseEntity = this.validationErrorService.validationErrorMessage(result);
        return responseEntity.orElseGet(() -> {
            userService.createUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        });
    }

}
