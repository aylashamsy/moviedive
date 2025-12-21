package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.TmdbMovieDto;
import org.springframework.web.bind.annotation.PathVariable;

public interface IMovieController
{
    public MovieDto findbyid(@PathVariable (name="id") long id);
    public MovieDto findbyName(@PathVariable (name="name") String name);
    public MovieDto randomFind();
}