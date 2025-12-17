package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponseDto {

    private Long id;
    private Long eventId;
    private Long userId;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;
    private LocalDateTime canceledAt;
}
