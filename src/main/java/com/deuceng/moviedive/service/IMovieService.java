package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.TmdbMovieDto;

public interface IMovieService
{
    public TmdbMovieDto findbyid(long id);
}
