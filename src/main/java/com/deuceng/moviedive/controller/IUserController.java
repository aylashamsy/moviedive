package com.deuceng.moviedive.controller;


import com.deuceng.moviedive.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


public interface IUserController {
    ResponseEntity<User> createUser(User user);

    ResponseEntity<User> getUser(Long id);

    ResponseEntity<List<User>> getAllUsers();
}
