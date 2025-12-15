package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IUserController;
import com.deuceng.moviedive.dto.DtoUser;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.service.IUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public DtoUser createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @Override
    @PostMapping(path = "/save_users")
    public String saveUsers(List<User> users)
    {
        return userService.saveUsers(users);
    }

    @Override
    @GetMapping(path="/get_user/{id}")
    public DtoUser getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Override
    @GetMapping(path="/get_all_user")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    @PostMapping(path="auth/register")
    public DtoUser saveEmployee(@RequestBody User user)
    {
        if(user == null)
        {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION,"Bir değer girmediniz",user));
        }
        return userService.saveUser(user);
    }

    @Override
    @PostMapping(path ="auth/login")
    public String login(@RequestBody RequestLogin request, HttpSession session)
    {
        return userService.login(request, session);
    }

    @Override
    @GetMapping(path="auth/me")
    public User me(HttpSession session)
    {
        return userService.me(session);
    }

    @Override
    @PostMapping(path="auth/logout")
    public String logout(HttpSession session)
    {
        return userService.logout(session);
    }
}
