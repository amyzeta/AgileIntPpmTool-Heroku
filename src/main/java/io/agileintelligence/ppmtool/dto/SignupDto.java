package io.agileintelligence.ppmtool.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignupDto {

    @Email(message="Username needs to be an email")
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message="Full name is required")
    private String fullName;

    @NotBlank(message="Password is required")
    private String password;

    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
