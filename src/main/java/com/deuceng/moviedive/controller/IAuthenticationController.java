package com.deuceng.moviedive.controller;


import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface IAuthenticationController {
    public String saveUsers(@RequestBody List<RegisterRequest> users);
    public RegisterRequest getUser(@PathVariable Long id);
    List<User> getAllUsers();
    public RegisterResponse register(RegisterRequest user);
    public boolean login(@RequestBody RequestLogin request, HttpSession session);
    public User me(HttpSession session);
    public String logout(HttpSession session);
    public void deleteme(HttpSession session);
    public void deleteUser(HttpSession session, @PathVariable (name="id") Long id);
    public DtoUpdateUserRequest getUserByUserName(@PathVariable (name="username")String username);
}
