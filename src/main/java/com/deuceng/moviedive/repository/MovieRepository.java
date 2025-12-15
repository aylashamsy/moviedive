package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>
{
    boolean existsByTmdbId(Long tmdbId);
}
