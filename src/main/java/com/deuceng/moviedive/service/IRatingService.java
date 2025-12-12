package com.deuceng.moviedive.service;

import com.deuceng.moviedive.entity.Rating;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


public interface IRatingService {

    Rating addOrUpdateRating(Long userId, Long movieId, double score, String review);

    void deleteRating(Long userId, Long movieId);

    List<Rating> getMovieRatings(Long movieId);

    List<Rating> getUserRatings(Long userId);

    Rating getCommentWithDate(Long userId, Long movieId, Date commentedAt);
}
