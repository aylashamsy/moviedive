package com.deuceng.moviedive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {

    private String title;
    private String description;
    private String locationName;
    private Long cityId;


    private Long movieId;

    private String eventDateTime;
}
