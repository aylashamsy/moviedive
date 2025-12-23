package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.TmdbMovieDto;
import com.deuceng.moviedive.entity.Movie;

import java.util.List;

public interface IMovieService
{
    public MovieDto findbyid(Long id);
    public MovieDto findbyName(String name);
    public MovieDto randomFind();
    public List<Movie> importAllMovies();

    }
