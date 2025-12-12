package com.deuceng.moviedive.dto;


import lombok.Data;

@Data
public class RatingRequestDto {
    private Long userId;
    private Long movieId;
    private double score;
    private String comment;
    private String commentedAt;
}
