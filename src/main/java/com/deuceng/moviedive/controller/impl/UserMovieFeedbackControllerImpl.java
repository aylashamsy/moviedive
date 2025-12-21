package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IUserMovieFeedbackController;
import com.deuceng.moviedive.dto.DtoUserMovieFeedback;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.service.IUserMovieFeedbackService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rest/api/movie_dive/rating/")
@RequiredArgsConstructor
public class UserMovieFeedbackControllerImpl implements IUserMovieFeedbackController {

    private final IUserMovieFeedbackService userMovieFeedbackService;

    @Override
    @PostMapping("ratemovie/{movie_id}/{score}")
    public DtoUserMovieFeedback rateMovie(HttpSession session, @PathVariable(name="movie_id") Long movie_id,@PathVariable (name="score")double score) {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        return userMovieFeedbackService.addorupdateRating(user_id, movie_id, score);
    }

    @Override
    @DeleteMapping(path="deleterating/{movie_id}")
    public void deleteRating(HttpSession session, @PathVariable(name="movie_id") Long movie_id) {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        userMovieFeedbackService.deleteRating(user_id,movie_id);
    }

    @Override
    @PutMapping(path="updaterating/{movie_id}/{score}")
    public DtoUserMovieFeedback updateRating(HttpSession session, @PathVariable(name="movie_id") Long movie_id, @PathVariable (name="score")double score)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        return userMovieFeedbackService.addorupdateRating(user_id,movie_id,score);
    }

    @Override
    @GetMapping(path="getrating/{user_id}/{movie_id}")
    public DtoUserMovieFeedback getRating(@PathVariable(name="user_id") Long user_id, @PathVariable(name="movie_id") Long movie_id)
    {
        return userMovieFeedbackService.getUserRating(user_id,movie_id);
    }

    @Override
    @PostMapping(path="commentmovie/{movie_id}")
    public DtoUserMovieFeedback commentMovie(HttpSession session, @PathVariable(name="movie_id") Long movie_id, @RequestBody String comment)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        return userMovieFeedbackService.commentMovie(user_id,movie_id, comment);
    }

    //Aslında comment columnlarına null setliyoruz(yani putmapping)
    @Override
    @DeleteMapping(path="deletecomment/{movie_id}/{comment_id}")
    public void DeleteMovieComment(HttpSession session, @PathVariable(name="movie_id") Long movie_id,@PathVariable(name="comment_id") Long comment_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        userMovieFeedbackService.DeleteMovieComment(user_id, movie_id, comment_id);
    }

    @Override
    @GetMapping("/getcomments/{user_id}/{movie_id}")
    public List<DtoUserMovieFeedback> getMovieComments(@PathVariable (name="user_id")Long user_id, @PathVariable(name="movie_id") Long movie_id){
        return userMovieFeedbackService.getMovieComments(user_id,movie_id);
    }

    @Override
    @GetMapping("/getallcomments/{movie_id}")
    public List<DtoUserMovieFeedback> getMovieAllComments(@PathVariable(name="movie_id") Long movie_id)
    {
        return userMovieFeedbackService.getMovieAllComments(movie_id);
    }

}