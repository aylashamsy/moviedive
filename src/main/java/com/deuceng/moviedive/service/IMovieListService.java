package com.deuceng.moviedive.service;

import com.deuceng.moviedive.entity.MovieList;

import java.util.List;

public interface IMovieListService {

    MovieList createList(Long userId, MovieList list);

    List<MovieList> getListsByUserId(Long userId);

    MovieList getListById(Long id);

    void deleteList(Long id);
}
