package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.DtoUser;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IUserService {

    DtoUser getUserById(Long id);

    List<User> getAllUsers();

    DtoUser saveUser(User user);

    String saveUsers(List<User> users);

    public String login(RequestLogin request, HttpSession session);
    public User me(HttpSession session);
    public String logout(HttpSession session);

}
