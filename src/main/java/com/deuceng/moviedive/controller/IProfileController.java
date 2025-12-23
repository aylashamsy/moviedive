package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.PublicUserProfileDto;
import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;


public interface IProfileController
{
    public RegisterResponse updateUser(HttpSession session, @RequestBody RegisterRequest request);
    public RegisterResponse getMe(HttpSession session);
    public RegisterResponse getUser(Long id);
    public PublicUserProfileDto getUserByUsername(String name);
}
