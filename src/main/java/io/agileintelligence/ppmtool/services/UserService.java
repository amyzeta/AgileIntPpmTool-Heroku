package io.agileintelligence.ppmtool.services;

import io.agileintelligence.ppmtool.domain.User;
import io.agileintelligence.ppmtool.dto.SignupDto;
import io.agileintelligence.ppmtool.exceptions.ValidationException;
import io.agileintelligence.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User createUser(SignupDto newUser) {
        final String username = newUser.getUsername();
        User existingUser = getUser(username);
        if (existingUser != null) {
            throw new ValidationException("username", String.format("Username '%s' already exists", username));
        }
        User user = toUser(newUser);
        return userRepository.save(user);
    }

    public User updateUser(SignupDto updatedUser) {
        final String username = updatedUser.getUsername();
        User existingUser = getUser(username);
        if (existingUser == null) {
            throw new ValidationException("username", String.format("Username '%s' does not belong to an existing user", username));
        }
        User user = toUser(updatedUser);
        return userRepository.save(user);
    }

    private User getUser(final String username) {
        return userRepository.findByUsername(username);
    }

    private User toUser(final SignupDto newUser) {
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setEncryptedPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        user.setFullName(newUser.getFullName());
        return user;
    }

}
