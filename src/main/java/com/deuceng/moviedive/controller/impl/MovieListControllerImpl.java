package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IMovieListController;
import com.deuceng.moviedive.entity.MovieList;
import com.deuceng.moviedive.service.IMovieListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rest/api/movie_dive/lists")
@RequiredArgsConstructor
public class MovieListControllerImpl implements IMovieListController {

    @Autowired
    private IMovieListService movieListService;

    @Override
    @PostMapping("createlist/{userId}")
    public MovieList createList(
            @PathVariable Long userId,
            @RequestBody MovieList list) {
        return movieListService.createList(userId, list);
    }

    @Override
    @GetMapping("getuserlists/{userId}")
    public List<MovieList> getUserLists(@PathVariable Long userId) {
        return movieListService.getListsByUserId(userId);
    }

    @Override
    @GetMapping("getlist/{id}")
    public MovieList getList(@PathVariable Long id) {
        return movieListService.getListById(id);
    }

    @Override
    @DeleteMapping("deletelist/{id}")
    public void deleteList(@PathVariable Long id) {
        movieListService.deleteList(id);
    }
}
