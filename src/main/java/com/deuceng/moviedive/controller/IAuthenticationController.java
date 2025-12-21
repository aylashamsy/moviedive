package com.deuceng.moviedive.controller;


import com.deuceng.moviedive.dto.DtoUpdateUserRequest;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface IAuthenticationController {
    public String saveUsers(@RequestBody List<DtoUpdateUserRequest> users);
    public DtoUpdateUserRequest getUser(@PathVariable Long id);
    List<User> getAllUsers();
    public DtoUpdateUserRequest register(User user);
    public boolean login(@RequestBody RequestLogin request, HttpSession session);
    public User me(HttpSession session);
    public String logout(HttpSession session);
    public void deleteme(HttpSession session);
    public void deleteUser(HttpSession session, @PathVariable (name="id") Long id);
}
