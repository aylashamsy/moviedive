package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.DtoUserMovieFeedback;
import com.deuceng.moviedive.entity.UserMovieFeedback;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;

public interface IUserMovieFeedbackService
{
    public void deleteRating(Long user_id, Long movie_id);
    public DtoUserMovieFeedback addorupdateRating(Long user_id, Long movie_id, double score);
    public DtoUserMovieFeedback getUserRating(Long user_id, Long movie_id);
    public DtoUserMovieFeedback commentMovie(Long user_id, Long movie_id, String comment);
    public void DeleteMovieComment(Long user_id, Long movie_id, Long comment_id);
    public List<DtoUserMovieFeedback> getMovieComments(Long user_id,Long movie_id);
    public List<DtoUserMovieFeedback> getMovieAllComments(Long movie_id);
}
