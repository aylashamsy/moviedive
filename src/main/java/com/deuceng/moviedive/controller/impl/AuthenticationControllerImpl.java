package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IAuthenticationController;
import com.deuceng.moviedive.dto.DtoUpdateUserRequest;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.Role;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.service.IAuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.deuceng.moviedive.exception.MessageType.NO_RECORD_EXIST;

@RestController
@RequestMapping(path="rest/api/movie_dive/users/")
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements IAuthenticationController {

   @Autowired
    private final IAuthenticationService AuthenticationService;


    @Override
    @PostMapping(path = "save_users")
    public String saveUsers(@RequestBody List<DtoUpdateUserRequest> users)
    {
        if(users.isEmpty())
        {
            throw new BaseException(new ErrorMessage(NO_RECORD_EXIST,"users listesi boş." ,null));
        }
        return AuthenticationService.saveUsers(users);
    }

    @Override
    @GetMapping(path="getuserbyid/{id}")
    public DtoUpdateUserRequest getUser(@PathVariable Long id) {
        return AuthenticationService.getUserById(id);
    }

    @Override
    @GetMapping(path="get_all_user")
    public List<User> getAllUsers() {
        return AuthenticationService.getAllUsers();
    }

    @Override
    @PostMapping(path="auth/register")
    public DtoUpdateUserRequest register(@RequestBody User user)
    {
        if(user == null)
        {
            throw new BaseException(new ErrorMessage(MessageType.GENERAL_EXCEPTION,"Bir değer girmediniz",user));
        }
        return AuthenticationService.register(user);
    }

    @Override
    @PostMapping(path ="auth/login")
    public boolean login(@RequestBody @Valid RequestLogin request, HttpSession session)
    {
        return AuthenticationService.login(request, session);
    }

    @Override
    @GetMapping(path="auth/me")
    public User me(HttpSession session)
    {
        return AuthenticationService.me(session);
    }

    @Override
    @PostMapping(path="auth/logout")
    public String logout(HttpSession session)
    {
        return AuthenticationService.logout(session);
    }

    @Override
    @DeleteMapping(path="auth/deleteme")
    public void deleteme(HttpSession session)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        AuthenticationService.deleteUser(user_id);
        session.invalidate();
    }

    @Override
    @DeleteMapping(path="auth/delete/{id}")
    public void deleteUser(HttpSession session, @PathVariable (name="id") Long id)
    {
        Role role = (Role) session.getAttribute("ROLE");
        Long admin_id = (Long) session.getAttribute("USER_ID");
        if (admin_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        if (role == null || !role.name().equals("ADMIN"))
        {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Yetkisiz erişim", id));
        }
        AuthenticationService.deleteUser(id);
    }

    @Override
    @GetMapping(path="getuserbyusername/{username}")
    public DtoUpdateUserRequest getUserByUserName(@PathVariable (name="username")String username){
        return AuthenticationService.getUserByUserName(username);
    }
}
