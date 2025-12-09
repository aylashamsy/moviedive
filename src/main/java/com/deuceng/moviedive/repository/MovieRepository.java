package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>
{

}
