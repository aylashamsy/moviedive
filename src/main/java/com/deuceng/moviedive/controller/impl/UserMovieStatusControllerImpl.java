package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IUserMovieStatusController;
import com.deuceng.moviedive.dto.DtoUserMovieStatus;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.entity.UserMovieStatus;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IUserMovieStatusService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="rest/api/movie_dive/lists")
public class UserMovieStatusControllerImpl implements IUserMovieStatusController
{
    @Autowired
    private IUserMovieStatusService userMovieStatusService;


    @GetMapping(path="/isliked/{user_id}/{movie_id}")
    public boolean isLiked(@PathVariable(name="user_id") Long user_id, @PathVariable(name="movie_id") Long movie_id)
    {
        return userMovieStatusService.Isliked(user_id, movie_id);
    }

    @GetMapping(path="get/relation_type/{user_id}/{movie_id}")
    public String getRelationType(@PathVariable(name="user_id") Long user_id, @PathVariable(name="movie_id") Long movie_id)
    {
        return userMovieStatusService.getRelationType(user_id, movie_id);
    }

    @GetMapping(path="getLikedMovies/{user_id}")
    public List<DtoUserMovieStatus> getLikedMovies(@PathVariable Long user_id)
    {
        return userMovieStatusService.getLikedMovies(user_id);
    }

    @GetMapping(path = "getWatchedMovie/{user_id}")
    public List<DtoUserMovieStatus> getWatchedMovies(@PathVariable(name = "user_id") Long user_id)
    {
        return userMovieStatusService.getWatchedMovies(user_id);
    }


    @GetMapping(path = "getWatchlaterMovie/{user_id}")
    public List<DtoUserMovieStatus> getWatchLaterMovies(@PathVariable(name = "user_id") Long user_id)
    {
        return userMovieStatusService.getWatchLaterMovies(user_id);
    }

    @PostMapping(path="/status/{user_id}/{movie_id}")
    public DtoUserMovieStatus setStatus(@PathVariable(name="user_id") Long user_id, @PathVariable(name="movie_id") Long movie_id)
    {
        return userMovieStatusService.setStatus(user_id, movie_id);
    }

    @PostMapping(path="setwatched/relation_type/{movie_id}")
    public void setWatched(HttpSession session, @PathVariable(name="movie_id") Long movie_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        userMovieStatusService.setWatched(user_id,movie_id);
    }
    @PostMapping(path="setwatchlater/relation_type/{movie_id}")
    public void setWatchLater(HttpSession session, @PathVariable(name="movie_id") Long movie_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
         userMovieStatusService.setWatchLater(user_id,movie_id);
    }


    @PostMapping(path="setdisliked/{movie_id}")
    public void setDisLiked(HttpSession session, @PathVariable(name="movie_id") Long movie_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        userMovieStatusService.setDisLiked(user_id, movie_id);
    }

    @PostMapping(path="setliked/{movie_id}")
    public void setLiked(HttpSession session, @PathVariable(name="movie_id") Long movie_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED,  " Giriş yapmalısınız", null));
        }
        userMovieStatusService.setLiked(user_id, movie_id);
    }


    @DeleteMapping(path="deletewatched/{movie_id}")
    public void deleteWatched(HttpSession session, @PathVariable(name="movie_id") Long movie_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        userMovieStatusService.deleteWatched(user_id,movie_id);
    }

    @DeleteMapping(path="deletewatchlater/{movie_id}")
    public void deleteWatchLater(HttpSession session, @PathVariable(name="movie_id") Long movie_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        userMovieStatusService.deleteWatchLater(user_id,movie_id);
    }

}
