package com.deuceng.moviedive.dto;

import lombok.Data;

import java.util.List;


@Data
public class TmdbMovieResponseDto {

    private Integer page;
    private List<TmdbMovieDto> results;
    private final Integer total_pages=500;
    private Integer total_results;
}
