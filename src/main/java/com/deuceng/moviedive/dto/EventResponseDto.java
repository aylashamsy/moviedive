package com.deuceng.moviedive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {

    private Long eventId;
    private String eventName;
    private String country;
    private String city;
    private String addressLine;
    private LocalDateTime eventDate;
    private LocalDateTime creationTime;

    private Long movieId;
    private Long creatorUserId;

    private Long activeRegistrationCount;
}
