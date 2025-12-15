package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.TmdbMovieDto;
import org.springframework.web.bind.annotation.PathVariable;

public interface IMovieController
{
    public TmdbMovieDto findbyid(@PathVariable (name="id") long id);
}