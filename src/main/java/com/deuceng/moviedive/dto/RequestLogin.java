package com.deuceng.moviedive.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogin
{
    @Email(message = "Geçerli bir email giriniz")
    @NotBlank(message = "Email boş olamaz")
    private String email;

    @NotBlank(message = "Password boş olamaz")
    @Size(min = 3, max = 30, message = "Password 5–30 karakter olmalı")
    private String password;
}
