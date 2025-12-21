package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import com.deuceng.moviedive.dto.UserResponseDto;

public interface IProfileServices
{
    public RegisterResponse updateProfile(Long id, RegisterRequest request);
    public RegisterRequest getUser(Long id);
    public RegisterRequest findByUsername(String name);
}
