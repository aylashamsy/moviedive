package com.deuceng.moviedive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String locationName;

    private Long cityId;
    private String cityName;

    private Long movieId;      // nullable
    private String movieTitle; // nullable

    private String eventDateTime;

    private Long createdByUserId;
    private String createdByUsername;

    private long registeredCount;
}
