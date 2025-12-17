package com.deuceng.moviedive.controller;


import com.deuceng.moviedive.dto.DtoUser;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface IUserController {
    public String saveUsers(@RequestBody List<DtoUser> users);
    public DtoUser getUser(@PathVariable Long id);
    List<User> getAllUsers();
    public DtoUser register(User user);
    public String login(@RequestBody RequestLogin request, HttpSession session);
    public User me(HttpSession session);
    public String logout(HttpSession session);
}
