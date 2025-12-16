package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.TmdbMovieDto;

public interface IMovieService
{
    public MovieDto findbyid(Long id);
    public MovieDto findbyName(String name);
}
