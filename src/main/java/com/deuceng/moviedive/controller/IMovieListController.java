package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.entity.MovieList;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IMovieListController {

    MovieList createList(Long userId, MovieList list);

    List<MovieList> getUserLists(Long userId);

    MovieList getList(Long id);

    void deleteList(Long id);
}
