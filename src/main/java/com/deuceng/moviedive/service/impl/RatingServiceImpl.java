package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.Rating;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.repository.RatingRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements IRatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Override
    public Rating addOrUpdateRating(Long userId, Long movieId, double score, String review) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Rating rating = ratingRepository
                .findByUser_IdAndMovie_Id(userId, movieId)
                .orElse(new Rating());

        rating.setUser(user);
        rating.setMovie(movie);
        rating.setScore(score);
        rating.setComment(review);
        rating.setCommentedAt(new Date());

        return ratingRepository.save(rating);
    }

    @Override
    public void deleteRating(Long userId, Long movieId) {
        Rating rating = ratingRepository
                .findByUser_IdAndMovie_Id(userId, movieId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        ratingRepository.delete(rating);
    }

    @Override
    public List<Rating> getMovieRatings(Long movieId) {
        return ratingRepository.findByMovie_Id(movieId);
    }

    @Override
    public List<Rating> getUserRatings(Long userId) {
        return ratingRepository.findByUser_Id(userId);
    }

    @Override
    public Rating getCommentWithDate(Long userId, Long movieId, Date commentedAt) {
        // Eğer tarih bazlı unique kayıt gerekiyorsa custom query eklenir.
        return ratingRepository.findByUser_IdAndMovie_Id(userId, movieId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
    }
}
