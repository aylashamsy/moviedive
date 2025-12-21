package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.Role;
import lombok.Data;

@Data
public class RegisterResponse {
    private String username;
    private String firstname;
    private String surname;
    private String email;
    private String phoneNumber;
    private String cityName;
    private Role role;
}
