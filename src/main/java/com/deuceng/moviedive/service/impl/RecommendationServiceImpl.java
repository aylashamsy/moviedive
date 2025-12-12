package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.entity.*;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.repository.RatingRepository;
import com.deuceng.moviedive.repository.RecommendationLogRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements IRecommendationService {

    private final RecommendationLogRepository recommendationLogRepository;
    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Override
    public List<Movie> getRecommendationsForUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kullanıcının tüm rating kayıtlarını çek
        List<Rating> ratings = ratingRepository.findByUser_Id(userId);

        // Genre'lere göre puan ortalaması yapan basit algoritma
        Map<Genre, Double> scoreMap = new HashMap<>();

        for (Rating r : ratings) {
            for (Genre g : r.getMovie().getGenres()) {
                scoreMap.merge(g, r.getScore(), Double::sum);
            }
        }

        // genre'leri skora göre sırala
        List<Genre> favoriteGenres = scoreMap.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // en sevdiği genre’dan 10 film getir
        List<Movie> movies = movieRepository.findTop10ByGenresIn(favoriteGenres);

        // LOG kaydı
        RecommendationLog log = new RecommendationLog();
        log.setUser(user);
        log.setParameters("auto-recommendation");
        log.setGeneratedAt(LocalDateTime.now());
        log.setResultCount(movies.size());

        recommendationLogRepository.save(log);

        return movies;
    }

    @Override
    public List<RecommendationLog> getHistory(Long userId) {
        return recommendationLogRepository.findByUser_Id(userId);
    }
}
