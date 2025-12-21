package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IMovieController;
import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.TmdbMovieDto;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.service.IMovieService;
import com.deuceng.moviedive.service.ITmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="rest/api/movie_dive/")
public class MovieControllerImpl implements IMovieController
{
    @Autowired
    private IMovieService movieService;
    @Autowired
    private ITmdbService tmdbService;

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
    @PostMapping(path = "/import/trending-week")
    public String importTrendingWeek() {
        tmdbService.importTrendingMoviesOfWeekTop20();
        return "Trending week top20 imported.";
    }
    @GetMapping(path = "/movies/latest20")
    public List<Movie> findTop20ByOrderByIdAsc() {
        return tmdbService.trendingMoviesOfWeekTop20();
    }
}
