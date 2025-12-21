package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.dto.DtoUserMovieFeedback;
import com.deuceng.moviedive.entity.UserMovieFeedback;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMovieFeedbackRepository extends JpaRepository<UserMovieFeedback, Long> {

    Optional<UserMovieFeedback> findByUser_IdAndMovie_Id(Long userId, Long movieId);

    List<UserMovieFeedback> findByMovie_Id(Long movieId);

    List<UserMovieFeedback> findByUser_Id(Long userId);

    @Modifying
    @Transactional
    @Query("""
    DELETE FROM UserMovieFeedback f
    WHERE f.user.id = :userId
      AND f.movie.id = :movieId
      AND f.comment.id = :commentId
""")
    int deleteByUserMovieAndComment(
            @Param("userId") Long userId,
            @Param("movieId") Long movieId,
            @Param("commentId") Long commentId
    );

    @Query("""
    SELECT f
    FROM UserMovieFeedback f
    WHERE f.user.id = :userId
      AND f.movie.id = :movieId
      AND f.comment IS NOT NULL
    ORDER BY f.comment.commentedAt DESC
""")
    List<UserMovieFeedback> findAllCommentsByUserAndMovie(
            @Param("userId") Long userId,
            @Param("movieId") Long movieId
    );

    @Query("""
    SELECT f
    FROM UserMovieFeedback f
    WHERE f.movie.id = :movieId
      AND f.comment IS NOT NULL
    ORDER BY f.comment.commentedAt DESC
""")
    List<UserMovieFeedback> findAllCommentsByMovie(
            @Param("movieId") Long movieId
    );


}