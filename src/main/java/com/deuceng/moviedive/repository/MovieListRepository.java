package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.MovieList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieListRepository extends JpaRepository<MovieList, Long> {

    // Bir kullanıcının sahip olduğu listeleri getirir
    List<MovieList> findByOwner_Id(Long userId);
}

