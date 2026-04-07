package com.example.jwt.auth.services;

import com.example.jwt.auth.entities.User;
import com.example.jwt.auth.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    // with pagination
    public Page<User> allUsers(Pageable pageable){
        return  userRepository.findAll(pageable);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }
}
