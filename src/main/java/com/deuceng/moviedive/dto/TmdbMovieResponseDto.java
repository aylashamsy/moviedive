package com.deuceng.moviedive.dto;

import lombok.Data;

import java.util.List;


@Data
public class TmdbMovieResponseDto {

    private int page;
    private List<TmdbMovieDto> results;
    private int total_pages;
    private int total_results;
}
