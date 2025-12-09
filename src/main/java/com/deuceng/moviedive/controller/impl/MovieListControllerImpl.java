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
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class MovieListControllerImpl implements IMovieListController {

    @Autowired
    private IMovieListService movieListService;

    @Override
    @PostMapping("/{userId}")
    public ResponseEntity<MovieList> createList(
            @PathVariable Long userId,
            @RequestBody MovieList list) {
        return ResponseEntity.ok(movieListService.createList(userId, list));
    }

    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MovieList>> getUserLists(@PathVariable Long userId) {
        return ResponseEntity.ok(movieListService.getListsByUserId(userId));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MovieList> getList(@PathVariable Long id) {
        return ResponseEntity.ok(movieListService.getListById(id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        movieListService.deleteList(id);
        return ResponseEntity.noContent().build();
    }
}
