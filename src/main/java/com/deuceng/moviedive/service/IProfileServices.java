package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.DtoUpdateUserRequest;
import com.deuceng.moviedive.dto.UserResponseDto;

public interface IProfileServices
{
    public UserResponseDto updateProfile(Long id, DtoUpdateUserRequest request);
    public DtoUpdateUserRequest getUser(Long id);
    public DtoUpdateUserRequest findByUsername(String name);
}
