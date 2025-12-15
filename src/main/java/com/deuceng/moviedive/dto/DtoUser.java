package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoUser
{
    private String username;
    private String firstname;
    private String surname;
    private String email;
    private Role role;
}
