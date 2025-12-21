package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.TmdbMovieDto;
import com.deuceng.moviedive.entity.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface IMovieController
{
    public MovieDto findbyid(@PathVariable (name="id") long id);
    public MovieDto findbyName(@PathVariable (name="name") String name);
    public MovieDto randomFind();

    @PostMapping(path = "/import/trending-week")
    String importTrendingWeek();

    @GetMapping(path = "/movies/latest20")
    List<Movie> findTop20ByOrderByIdAsc();
}