package com.deuceng.moviedive.dto;


import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class DtoUserMovieFeedback {
    private User user;
    private Movie movie;
    private Double score;
    private String text;
    private LocalDateTime commentedAt;
    private Long comment_id;
}