package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.DtoUserMovieFeedback;
import com.deuceng.moviedive.entity.UserMovieFeedback;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IUserMovieFeedbackController
{
    public DtoUserMovieFeedback rateMovie(HttpSession session, @PathVariable (name="movie_id")Long movie_id, @PathVariable (name="score")double score);
    public void deleteRating(HttpSession session, @PathVariable (name="movie_id")Long movie_id);
    public DtoUserMovieFeedback updateRating(HttpSession session, @PathVariable(name="movie_id") Long movie_id, @PathVariable (name="score")double score);
    public DtoUserMovieFeedback getRating(@PathVariable(name="user_id") Long user_id, @PathVariable(name="movie_id") Long movie_id);
    public DtoUserMovieFeedback commentMovie(HttpSession session, @PathVariable(name="movie_id") Long movie_id, @RequestBody String comment);
    public void DeleteMovieComment(HttpSession session, @PathVariable(name="movie_id") Long movie_id,@PathVariable (name="comment_id")Long comment_id);
    public List<DtoUserMovieFeedback> getMovieComments(@PathVariable (name="user_id")Long user_id, @PathVariable(name="movie_id") Long movie_id);
    public List<DtoUserMovieFeedback> getMovieAllComments(@PathVariable(name="movie_id") Long movie_id);

}
