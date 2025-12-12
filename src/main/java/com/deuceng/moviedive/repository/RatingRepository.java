package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUser_IdAndMovie_Id(Long userId, Long movieId);

    List<Rating> findByMovie_Id(Long movieId);

    List<Rating> findByUser_Id(Long userId);
}
