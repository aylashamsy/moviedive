package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import com.deuceng.moviedive.dto.UserResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;


public interface IProfileController
{
    public RegisterResponse updateUser(HttpSession session, @RequestBody RegisterRequest request);
    public RegisterRequest getMe(HttpSession session);
    public RegisterRequest getUser(Long id);
    public RegisterRequest getUserByUsername(String name);
}
