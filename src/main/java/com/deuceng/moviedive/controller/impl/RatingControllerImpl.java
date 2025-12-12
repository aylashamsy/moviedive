package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IRatingController;
import com.deuceng.moviedive.dto.RatingRequestDto;
import com.deuceng.moviedive.entity.Rating;
import com.deuceng.moviedive.service.IRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rest/api/movie_dive/ratings")
@RequiredArgsConstructor
public class RatingControllerImpl implements IRatingController {

    private final IRatingService ratingService;

    @Override
    @PostMapping("/rate")
    public ResponseEntity<Rating> rateMovie(@RequestBody RatingRequestDto request) {
        return ResponseEntity.ok(
                ratingService.addOrUpdateRating(
                        request.getUserId(),
                        request.getMovieId(),
                        request.getScore(),
                        request.getComment()
                )
        );
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> deleteRating(@RequestParam Long userId,
                                             @RequestParam Long movieId) {
        ratingService.deleteRating(userId, movieId);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Rating>> getMovieRatings(@PathVariable Long movieId) {
        return ResponseEntity.ok(ratingService.getMovieRatings(movieId));
    }

    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getUserRatings(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getUserRatings(userId));
    }
}
