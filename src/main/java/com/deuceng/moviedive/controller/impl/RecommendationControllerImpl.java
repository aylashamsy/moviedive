package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IRecommendationController;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.RecommendationLog;
import com.deuceng.moviedive.service.IRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/api/movie_dive/recommendation")
@RequiredArgsConstructor
public class RecommendationControllerImpl implements IRecommendationController {

    private final IRecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Movie>> getRecommendations(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.getRecommendationsForUser(userId));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<RecommendationLog>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.getHistory(userId));
    }
}
