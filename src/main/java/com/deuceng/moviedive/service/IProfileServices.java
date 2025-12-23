package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.ChangePasswordRequest;
import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;

public interface IProfileServices
{
    public RegisterResponse updateProfile(Long id, RegisterRequest request);
    public RegisterResponse getUser(Long id);
    public RegisterRequest findByUsername(String name);
    RegisterResponse changePassword(Long id, ChangePasswordRequest request);

}
