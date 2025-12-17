package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository  extends JpaRepository<Genre,Integer>
{

}
