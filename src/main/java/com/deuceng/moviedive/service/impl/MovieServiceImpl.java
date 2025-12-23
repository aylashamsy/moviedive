package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.TmdbMovieDto;
import com.deuceng.moviedive.entity.*;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.service.IMovieService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MovieServiceImpl implements IMovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public MovieDto findbyid(Long id)
    {
        Movie movie=movieRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Böyle bir film bulunamadı.",id)));
        MovieDto dtomovie=new MovieDto();

        BeanUtils.copyProperties(movie,dtomovie);
        dtomovie.setPoster_url(movie.getPoster_path());
        for(int i=0;i<movie.getGenres().size();i++)
        {
            dtomovie.getGenre_names().add(movie.getGenres().get(i).getName());
        }
        for(int i=0;i<movie.getDirectors().size();i++)
        {
            dtomovie.getDirector_names().add(movie.getDirectors().get(i).getName());
        }
        for(int i=0;i<movie.getCastMembers().size();i++)
        {
            dtomovie.getActors().add(movie.getCastMembers().get(i).getName());
        }
        return dtomovie;
    }

    @Override
    public MovieDto findbyName(String name)
    {
        if(movieRepository.findByTitle(name)==null)
        {
           throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Böyle bir film bulunamadı.",name));
        }
        Movie movie=movieRepository.findByTitle(name);
        MovieDto dtomovie=new MovieDto();

        BeanUtils.copyProperties(movie,dtomovie);
        dtomovie.setPoster_url(movie.getPoster_path());
        for(int i=0;i<movie.getGenres().size();i++)
        {
            dtomovie.getGenre_names().add(movie.getGenres().get(i).getName());
        }
        for(int i=0;i<movie.getDirectors().size();i++)
        {
            dtomovie.getDirector_names().add(movie.getDirectors().get(i).getName());
        }
        for(int i=0;i<movie.getCastMembers().size();i++)
        {
            dtomovie.getActors().add(movie.getCastMembers().get(i).getName());
        }
        return dtomovie;
    }

    @Override
    public MovieDto randomFind() {
        Random random = new Random();
        final long MAX_ID = 9501L;
        final int MAX_TRY = 200;
        Movie movie = new Movie();

        for (int i = 0; i < MAX_TRY; i++) {
            long temp = random.nextLong(MAX_ID);
            Optional<Movie> opt = movieRepository.findById(temp);
            if (opt.isPresent())
            {
                movie = opt.get();
                break;
            }
        }
        if (movie == null) {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Random film bulunamadı.",null));
        }
        MovieDto dtomovie = new MovieDto();
        BeanUtils.copyProperties(movie,dtomovie);
        dtomovie.setPoster_url(movie.getPoster_path());
        for(int i=0;i<movie.getGenres().size();i++)
        {
            dtomovie.getGenre_names().add(movie.getGenres().get(i).getName());
        }
        for(int i=0;i<movie.getDirectors().size();i++)
        {
            dtomovie.getDirector_names().add(movie.getDirectors().get(i).getName());
        }
        for(int i=0;i<movie.getCastMembers().size();i++)
        {
            dtomovie.getActors().add(movie.getCastMembers().get(i).getName());
        }
        return dtomovie;
    }

    @Override
    public List<Movie> importAllMovies() {
        return movieRepository.findAll();
}}
