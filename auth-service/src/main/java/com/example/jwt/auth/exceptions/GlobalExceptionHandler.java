package com.example.jwt.auth.exceptions;

import com.example.jwt.auth.dtos.ErrorAuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


// es una clase que maneja las excepciones generadas en el proyecto,
// es un aspecto de la arquitectura que se encarga de manejar las excepciones
// Es como un listener que se encarga de interceptar las excepciones y devolver un response
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ErrorAuthResponse handleBadCredentials(BadCredentialsException ex) {

        return new ErrorAuthResponse(
                400,
                "The username or password is incorrect"
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorAuthResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorAuthResponse(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage()
                ));
    }

}

