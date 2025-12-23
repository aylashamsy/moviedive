package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.Role;
import lombok.Data;

@Data
public class PublicUserProfileDto {
    private Long id;          // ✅ KRİTİK
    private String username;
    private String firstname;
    private String surname;
    private String cityName;
    private Role role;
}
