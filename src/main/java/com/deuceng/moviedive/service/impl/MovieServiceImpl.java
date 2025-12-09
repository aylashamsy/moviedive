package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.Dtomovie;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.service.IMovieService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements IMovieService {

    @Autowired
    private MovieRepository movieRepository;

    public Dtomovie findbyid(long id)
    {
        List<Movie> movieList=movieRepository.findAll();
        Dtomovie dtomovie=new Dtomovie();
        for(int i=0;i<movieList.size();i++)
        {
            Movie temp=movieList.get(i);
            if(temp.getId()==id)
            {
                BeanUtils.copyProperties(temp,dtomovie);
                break;
            }
        }
        return dtomovie;
    }
}
