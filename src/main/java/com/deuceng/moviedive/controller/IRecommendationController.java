package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.RecommendationLog;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IRecommendationController {

    ResponseEntity<List<Movie>> getRecommendations(Long userId);

    ResponseEntity<List<RecommendationLog>> getHistory(Long userId);
}
