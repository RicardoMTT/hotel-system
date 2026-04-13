package com.example.jwt.auth.services;

import com.example.jwt.auth.dtos.LoginRequest;
import com.example.jwt.auth.dtos.RegisterRequest;
import com.example.jwt.auth.dtos.RegisterResponse;
import com.example.jwt.auth.entities.User;
import com.example.jwt.auth.exceptions.EmailAlreadyExistsException;
import com.example.jwt.auth.kafka.producer.UserEventProducer;
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
    private final UserEventProducer userEventProducer;


    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserEventProducer userEventProducer

    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEventProducer = userEventProducer;
    }

    public RegisterResponse signup(RegisterRequest input) {
        if (userRepository.existsByEmail(input.email())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setFullName(input.fullName());
        user.setEmail(input.email());
        user.setPassword(passwordEncoder.encode(input.password()));

        User savedUser = userRepository.save(user);

        // Se publica un evento de usuario registrado
        userEventProducer.publishUserRegistered(
                input.email(),   // reemplaza con: user.getEmail() o request.email()
                input.fullName() // reemplaza con: user.getFullName() o request.fullName()
        );
        return new RegisterResponse(savedUser.getEmail(), savedUser.getFullName());
    }

    public User authenticate(LoginRequest input) {

        // Validas credenciales y lo guarda el resultado en el SecurityContext
        // Aca se llama al authenticationProvider que se encarga de autenticar el usuario
        // Que a su vez llama al userDetailsService para buscar el usuario, usa el userDetailsService
        // que es el que sabe buscar el usuario por el email
        // Lanza una exception si el usuario no existe
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );

        // Se ejecuta solo si el usuario existe
        return userRepository.findByEmail(input.email())
                .orElseThrow();
    }
}
