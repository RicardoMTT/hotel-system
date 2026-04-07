package com.example.jwt.auth.controllers;

import com.example.jwt.auth.entities.User;
import com.example.jwt.auth.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;


@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/all-users")
    public  ResponseEntity<List<User>> getAllUsers(
            @RequestParam(defaultValue = "natural") String orden
    ) {
        List<User> users = userService.findAll();

        if (orden.equals("email")) {
            System.out.println("Ordenando por email");
            // Usar comparator
            // No se declara una variable users porque se modifica la lista original
            users.sort(User.POR_EMAIL);
        }else{
            // Usando comparable
            Collections.sort(users);
        }

        return ResponseEntity.ok(users);
    }



    @GetMapping("")
    public ResponseEntity<Page<User>> allUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
            ) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;


        Pageable pageable = PageRequest.of(page,size,Sort.by(direction,sortBy));

        Page<User> users = userService.allUsers(pageable);

        return ResponseEntity.ok(users);
    }

}
