package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.Role;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String firstname;
    private String surname;
    private String phoneNumber;
    private String email;
    private Role role;
    //City city
}
