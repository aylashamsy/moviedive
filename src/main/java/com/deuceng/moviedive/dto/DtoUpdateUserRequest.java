package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoUpdateUserRequest
{
    private String username;
    private String password;
    private String firstname;
    private String surname;
    private String email;
    private String phoneNumber;
    private String address;
    private Role role;
}
