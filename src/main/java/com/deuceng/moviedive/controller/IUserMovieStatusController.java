package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.DtoUserMovieStatus;
import com.deuceng.moviedive.entity.UserMovieStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IUserMovieStatusController
{
    public boolean isLiked(@PathVariable Long user_id, @PathVariable Long movie_id);
    //Denemek için kullanılıyor
    public String getRelationType(Long user_id, Long movie_id);
    //Denemek için onun için login şartı yok
    public DtoUserMovieStatus setStatus(@PathVariable Long user_id, @PathVariable Long movie_id);
    public void setWatched(HttpSession session, @PathVariable Long movie_id);
    public void setWatchLater(HttpSession session, @PathVariable Long movie_id);
    public void setFavorite(HttpSession session, @PathVariable Long movie_id);
    public void setDisLiked(HttpSession session, @PathVariable Long movie_id);
    public void setLiked(HttpSession session, @PathVariable Long movie_id);
    public List<DtoUserMovieStatus> getFavoriteMovies(@PathVariable(name="user_id") Long user_id);
    public List<DtoUserMovieStatus> getWatchedMovies(@PathVariable(name="user_id") Long user_id);
    public List<DtoUserMovieStatus> getWatchLaterMovies(@PathVariable(name="user_id") Long user_id);
    public void deleteFavorite(HttpSession session, @PathVariable(name="movie_id") Long movie_id);
    public void deleteWatchLater(HttpSession session, @PathVariable(name="movie_id") Long movie_id);
    public void deleteWatched(HttpSession session, @PathVariable(name="movie_id") Long movie_id);
}
