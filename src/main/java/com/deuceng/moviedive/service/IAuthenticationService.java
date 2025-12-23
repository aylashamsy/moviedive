package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface IAuthenticationService {

    RegisterRequest getUserById(Long id);
    List<User> getAllUsers();
    RegisterResponse register(RegisterRequest dtoUser);
    String saveUsers(List<RegisterRequest> users);
    public boolean login(RequestLogin request, HttpSession session);
    public User me(HttpSession session);
    public String logout(HttpSession session);
    public void deleteUser(Long id);
    public DtoUpdateUserRequest getUserByUserName(String username);
        private void requireAdmin(HttpSession session) {
        String role = (String) session.getAttribute("ROLE");
        if (role == null || !role.equals("ADMIN")) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Admin required",role));
        }
    }
    private void requireVipOrAdmin(HttpSession session) {
        String role = (String) session.getAttribute("ROLE");
        if (role == null || !(role.equals("VIP") )) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "VIP required",role));
        }
    }
}