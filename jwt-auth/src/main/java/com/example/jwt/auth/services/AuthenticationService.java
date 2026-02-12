package com.example.jwt.auth.services;

import com.example.jwt.auth.dtos.LoginRequest;
import com.example.jwt.auth.dtos.RegisterRequest;
import com.example.jwt.auth.entities.User;
import com.example.jwt.auth.exceptions.EmailAlreadyExistsException;
import com.example.jwt.auth.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterRequest input) {
        if (userRepository.existsByEmail(input.email())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setFullName(input.fullName());
        user.setEmail(input.email());
        user.setPassword(passwordEncoder.encode(input.password()));

        return userRepository.save(user);
    }

    public User authenticate(LoginRequest input) {

        // Validas credenciales y lo guarda el resultado en el SecurityContext
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );

        return userRepository.findByEmail(input.email())
                .orElseThrow();
    }
}
