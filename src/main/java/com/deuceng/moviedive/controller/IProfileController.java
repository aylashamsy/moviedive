package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.DtoUpdateUserRequest;
import com.deuceng.moviedive.dto.UserResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;


public interface IProfileController
{
    public UserResponseDto updateUser(HttpSession session, @RequestBody DtoUpdateUserRequest request);
    public DtoUpdateUserRequest getMe(HttpSession session);
    public DtoUpdateUserRequest getUser(Long id);
    public DtoUpdateUserRequest getUserByUsername(String name);
}
