package com.deuceng.moviedive.service;

import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.RecommendationLog;

import java.util.List;

public interface IRecommendationService {
    List<Movie> getRecommendationsForUser(Long userId);
    List<RecommendationLog> getHistory(Long userId);

}
