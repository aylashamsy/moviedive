package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.RatingRequestDto;
import com.deuceng.moviedive.entity.Rating;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IRatingController {

    ResponseEntity<Rating> rateMovie(RatingRequestDto request);

    ResponseEntity<Void> deleteRating(Long userId, Long movieId);

    ResponseEntity<List<Rating>> getMovieRatings(Long movieId);

    ResponseEntity<List<Rating>> getUserRatings(Long userId);
}
