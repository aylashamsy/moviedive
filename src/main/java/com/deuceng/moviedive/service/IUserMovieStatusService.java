package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.DtoUserMovieStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


public interface IUserMovieStatusService
{
    public boolean Isliked(Long user_id, Long movie_id);
    public List<DtoUserMovieStatus> getLikedMovies(Long user_id);
    public String getRelationType(Long user_id, Long movie_id);
    public void setWatched(Long user_id, Long movie_id);
    public void setWatchLater(Long user_id, Long movie_id);
    public DtoUserMovieStatus setStatus(Long user_id, Long movie_id);
    public void setLiked(Long user_id, Long movie_id);
    public List<DtoUserMovieStatus> getWatchedMovies(Long user_id);
    public List<DtoUserMovieStatus> getWatchLaterMovies(Long user_id);
    public void setDisLiked(Long user_id, Long movie_id);
    public void deleteWatchLater(Long user_id,Long movie_id);
    public void deleteWatched(Long user_id,Long movie_id);
}
