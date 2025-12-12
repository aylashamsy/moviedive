package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Genre;
import com.deuceng.moviedive.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>
{
    List<Movie> findTop10ByGenresIn(List<Genre> genres);

}
