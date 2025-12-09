package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.entity.MovieList;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.repository.MovieListRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IMovieListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieListServiceImpl implements IMovieListService {

    @Autowired
    private  MovieListRepository movieListRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public MovieList createList(Long userId, MovieList list) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        list.setOwner(owner);
        return movieListRepository.save(list);
    }

    @Override
    public List<MovieList> getListsByUserId(Long userId) {
        return movieListRepository.findByOwner_Id(userId);
    }

    @Override
    public MovieList getListById(Long id) {
        return movieListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("List not found"));
    }

    @Override
    public void deleteList(Long id) {
        movieListRepository.deleteById(id);
    }
}

