package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.entity.MovieList;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IMovieListController {

    ResponseEntity<MovieList> createList(Long userId, MovieList list);

    ResponseEntity<List<MovieList>> getUserLists(Long userId);

    ResponseEntity<MovieList> getList(Long id);

    ResponseEntity<Void> deleteList(Long id);
}
