package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IMovieController;
import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.TmdbMovieDto;
import com.deuceng.moviedive.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="rest/api/movie_dive/")
public class MovieControllerImpl implements IMovieController
{
    @Autowired
    private IMovieService movieService;

    @Override
    @GetMapping(path="getbyid/{id}")
    public MovieDto findbyid(@PathVariable (name="id") long id)
    {
        return movieService.findbyid(id);
    }

    @Override
    @GetMapping(path="getbyname/{name}")
    public MovieDto findbyName(@PathVariable (name="name") String name)
    {
        return movieService.findbyName(name);
    }

}
