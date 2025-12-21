package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>
{
    boolean existsByTmdbId(Long tmdbId);

    Movie findByTitle(String name);

    Optional<Movie> findById(Long id);

    boolean existsById(Long id);
}
