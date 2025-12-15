package com.deuceng.moviedive.controller;


import com.deuceng.moviedive.dto.DtoUser;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface IUserController {
    DtoUser createUser(User user);

    String saveUsers(List<User> users);

    DtoUser getUser(Long id);
    List<User> getAllUsers();

    public DtoUser saveEmployee(User user);
    public String login(@RequestBody RequestLogin request, HttpSession session);
    public User me(HttpSession session);
    public String logout(HttpSession session);
}
