package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.DtoUserMovieFeedback;
import com.deuceng.moviedive.entity.Comment;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.entity.UserMovieFeedback;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.CommentRepository;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.repository.UserMovieFeedbackRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IUserMovieFeedbackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IUserMovieFeedbackServiceImpl implements IUserMovieFeedbackService {

    @Autowired
    private UserMovieFeedbackRepository userMovieFeedbackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    @Override
    public void deleteRating(Long user_id, Long movie_id){
        UserMovieFeedback rating = userMovieFeedbackRepository.findByUser_IdAndMovie_Id(user_id, movie_id).orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Böyle bir puanlama yok",user_id)));
        Movie movie= rating.getMovie();
        double oldRating = movie.getVote_average();
        int oldVoteCount = Integer.parseInt(movie.getVote_count());
        double userScore=rating.getScore();
        if(oldVoteCount==1)
        {
            movie.setVote_average(0.0);
            movie.setVote_count(String.valueOf(0));
        }
        else
        {
            double newRating = (oldRating * oldVoteCount - userScore) / (oldVoteCount-1);
            movie.setVote_average(newRating);
            movie.setVote_count(String.valueOf(oldVoteCount - 1));
        }
        userMovieFeedbackRepository.delete(rating);
        movieRepository.save(movie);
    }

    @Transactional
    @Override
    public DtoUserMovieFeedback addorupdateRating(Long user_id, Long movie_id, double score)
    {
        User user = userRepository.findById(user_id).orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Böyle bir kullanıcı yok",user_id)));
        Movie movie = movieRepository.findById(movie_id).orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Böyle bir film yok",movie_id)));
        Optional<UserMovieFeedback> rating = userMovieFeedbackRepository.findByUser_IdAndMovie_Id(user_id, movie_id);
        double oldRating = movie.getVote_average();
        int oldVoteCount = Integer.parseInt(movie.getVote_count());

        UserMovieFeedback feedback;
        if(rating.isPresent())
        {
            feedback = rating.get();
            double oldUserScore = feedback.getScore();

            double newRating = (oldRating * oldVoteCount - oldUserScore + score) / oldVoteCount;
            movie.setVote_average(newRating);
        }
        else
        {
            feedback = new UserMovieFeedback();
            feedback.setUser(user);
            feedback.setMovie(movie);

            double newRating = (oldRating * oldVoteCount + score) / (oldVoteCount + 1);
            movie.setVote_average(newRating);
            int new_vote_count=oldVoteCount+1;
            movie.setVote_count(String.valueOf(new_vote_count));
        }
        feedback.setScore(score);
        feedback.setUser(user);
        feedback.setMovie(movie);
        userMovieFeedbackRepository.save(feedback);
        movieRepository.save(movie);
        DtoUserMovieFeedback dtoUserMovieFeedback = new DtoUserMovieFeedback();
        dtoUserMovieFeedback.setUser(user);
        dtoUserMovieFeedback.setMovie(movie);
        dtoUserMovieFeedback.setScore(score);
        return dtoUserMovieFeedback;
    }


    @Override
    public DtoUserMovieFeedback getUserRating(Long user_id, Long movie_id)
    {
        UserMovieFeedback rating = userMovieFeedbackRepository.findByUser_IdAndMovie_Id(user_id, movie_id).orElseThrow(()-> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Böyle bir puanlama yok",user_id)));
        DtoUserMovieFeedback dtoUserMovieFeedback = new DtoUserMovieFeedback();
        dtoUserMovieFeedback.setUser(rating.getUser());
        dtoUserMovieFeedback.setMovie(rating.getMovie());
        dtoUserMovieFeedback.setScore(rating.getScore());
        return dtoUserMovieFeedback;
    }


    @Override
    public DtoUserMovieFeedback commentMovie(Long user_id, Long movie_id, String comment)
    {
        User user = userRepository.findById(user_id).orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Böyle bir kullanıcı yok",user_id)));
        Movie movie = movieRepository.findById(movie_id).orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Böyle bir film yok",movie_id)));
        UserMovieFeedback rating = new UserMovieFeedback();
        DtoUserMovieFeedback dtoUserMovieFeedback = new DtoUserMovieFeedback();
        Comment temp_comment=new Comment();
        temp_comment.setText(comment);
        LocalDateTime now = LocalDateTime.now();
        temp_comment.setCommentedAt(now);
        commentRepository.save(temp_comment);
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setComment(temp_comment);
        userMovieFeedbackRepository.save(rating);
        BeanUtils.copyProperties(rating,dtoUserMovieFeedback);
        dtoUserMovieFeedback.setText(comment);
        dtoUserMovieFeedback.setCommentedAt(now);
        dtoUserMovieFeedback.setComment_id(temp_comment.getId());
        return dtoUserMovieFeedback;
    }



    public void DeleteMovieComment(Long user_id, Long movie_id, Long comment_id)
    {
        int delete_comments_number = userMovieFeedbackRepository.deleteByUserMovieAndComment(user_id, movie_id,comment_id);
        if(delete_comments_number == 0)
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST,"Böyle bir yorum yok", user_id));
        }
        commentRepository.deleteById(comment_id);
    }



    public List<DtoUserMovieFeedback> getMovieComments(Long userId, Long movieId) {

        List<UserMovieFeedback> comments = userMovieFeedbackRepository.findAllCommentsByUserAndMovie(userId, movieId);
        if (comments.isEmpty())
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST,"Bu kullanıcı bu filme hiç yorum yapmamış", userId));
        }
        List<DtoUserMovieFeedback> dtocomments=new ArrayList<>();
        for(int i=0;i<comments.size();i++)
        {
            DtoUserMovieFeedback dtoUserMovieFeedback=new DtoUserMovieFeedback();
            dtoUserMovieFeedback.setUser(comments.get(i).getUser());
            dtoUserMovieFeedback.setMovie(comments.get(i).getMovie());
            dtoUserMovieFeedback.setScore(comments.get(i).getScore());
            dtoUserMovieFeedback.setText(comments.get(i).getComment().getText());
            dtoUserMovieFeedback.setCommentedAt(LocalDateTime.now());
            dtoUserMovieFeedback.setComment_id(comments.get(i).getId());
            dtocomments.add(dtoUserMovieFeedback);
        }
        return dtocomments;
    }


    public List<DtoUserMovieFeedback> getMovieAllComments(Long movieId)
    {
        List<UserMovieFeedback> comments = userMovieFeedbackRepository.findAllCommentsByMovie(movieId);
        if (comments.isEmpty())
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Bu filmin yorumu bolunumadı.", movieId));
        }
        List<DtoUserMovieFeedback> dtocomments=new ArrayList<>();
        for(int i=0;i<comments.size();i++)
        {
            DtoUserMovieFeedback dtoUserMovieFeedback=new DtoUserMovieFeedback();
            dtoUserMovieFeedback.setUser(comments.get(i).getUser());
            dtoUserMovieFeedback.setMovie(comments.get(i).getMovie());
            dtoUserMovieFeedback.setScore(comments.get(i).getScore());
            dtoUserMovieFeedback.setText(comments.get(i).getComment().getText());
            dtoUserMovieFeedback.setCommentedAt(LocalDateTime.now());
            dtoUserMovieFeedback.setComment_id(comments.get(i).getId());
            dtocomments.add(dtoUserMovieFeedback);
        }
        return dtocomments;
    }
}