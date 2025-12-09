package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IUserController;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rest/api/movie_dive/users")
@RequiredArgsConstructor
public class UserControllerImpl implements IUserController {

   @Autowired
    private final IUserService userService;

    @Override
    @PostMapping(path = "/save_user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @Override
    @GetMapping("/get_user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
