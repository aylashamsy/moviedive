package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.Dtomovie;
import org.springframework.web.bind.annotation.PathVariable;

public interface IMovieController
{
    public Dtomovie findbyid(long id);
}
