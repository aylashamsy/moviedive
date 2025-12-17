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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.deuceng.moviedive.exception.MessageType.NO_RECORD_EXIST;

@RestController
@RequestMapping("rest/api/movie_dive/users")
@RequiredArgsConstructor
public class UserControllerImpl implements IUserController {

   @Autowired
    private final IUserService userService;


    @Override
    @PostMapping(path = "/save_users")
    public String saveUsers(@RequestBody List<DtoUser> users)
    {
        if(users.isEmpty())
        {
            throw new BaseException(new ErrorMessage(NO_RECORD_EXIST,"users listesi boş." ,null));
        }
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
    public DtoUser register(@RequestBody User user)
    {
        if(user == null)
        {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION,"Bir değer girmediniz",user));
        }
        return userService.register(user);
    }

    @Override
    @PostMapping(path ="auth/login")
    public String login(@RequestBody @Valid RequestLogin request, HttpSession session)
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
