package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.DtoUserMovieStatus;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.RelationTypeEnum;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.entity.UserMovieStatus;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.repository.UserMovieStatusRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IUserMovieStatusService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RelationType;
import java.util.List;

@Service
public class UserMovieStatusServiceImpl implements IUserMovieStatusService
{
    @Autowired
    UserMovieStatusRepository userMovieStatusRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MovieRepository movieRepository;

    public boolean Isliked(Long user_id, Long movie_id)
    {
        return userMovieStatusRepository.findByUser_IdAndMovie_Id(user_id, movie_id)
                .map(UserMovieStatus::isLiked)
                .orElse(false);
    }

    public List<DtoUserMovieStatus> getLikedMovies(Long user_id)
    {
        List<DtoUserMovieStatus> liked_movie=userMovieStatusRepository.findByUserIdandLiked(user_id);
        if(liked_movie.isEmpty())
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Bu kullanıcının sevdiği film listesi yok",user_id));
        }
        return liked_movie;
    }

    public void setLiked(Long user_id, Long movie_id)
    {
        UserMovieStatus userMovieStatus=userMovieStatusRepository.findByUser_IdAndMovie_Id(user_id, movie_id).orElseGet(() ->
        {UserMovieStatus temp = new UserMovieStatus();
                Movie movie = movieRepository.getById(movie_id);
                User user = userRepository.getById(user_id);
                temp.setMovie(movie);
                temp.setUser(user);
                return temp;
        });
        userMovieStatus.setLiked(true);
        userMovieStatusRepository.save(userMovieStatus);
    }

    public void setDisLiked(Long user_id, Long movie_id)
    {
        UserMovieStatus userMovieStatus=userMovieStatusRepository.findByUser_IdAndMovie_Id(user_id, movie_id).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, " Bu film ile bu user'ın bir beğenme bağlantısı yok", movie_id)));
        userMovieStatus.setLiked(false);
        userMovieStatusRepository.save(userMovieStatus);
    }

    public String getRelationType(Long user_id, Long movie_id)
    {
        if(!userMovieStatusRepository.existsByUser_Id(user_id))
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST,"Kullanıcının hiçbir kaydı yok",user_id));
        }
        if(!userMovieStatusRepository.existsByMovie_Id(movie_id))
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST,"Kullanıcının bu filmle hiçbir kaydı yok",user_id));
        }
        UserMovieStatus userMovieStatus=userMovieStatusRepository.findByUser_IdAndMovie_Id(user_id, movie_id).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, " Bu film ile bu user'ın bir bağlantısı yok", movie_id)));
        return userMovieStatus.getRelation_type().toString();
    }

    @Transactional
    public void setWatched(Long user_id, Long movie_id)
    {
        UserMovieStatus userMovieStatus=userMovieStatusRepository.findByUser_IdAndMovie_Id(user_id, movie_id).orElseGet(() ->
        {UserMovieStatus temp = new UserMovieStatus();
            User user = userRepository.getById(user_id);
            Movie movie = movieRepository.getById(movie_id);
            temp.setUser(user);
            temp.setMovie(movie);
            temp.setRelation_type(RelationTypeEnum.WATCHED);
            return temp;
        });
        userMovieStatus.setRelation_type(RelationTypeEnum.WATCHED);
        userMovieStatusRepository.save(userMovieStatus);
    }

    @Transactional
    public void setWatchLater(Long user_id, Long movie_id)
    {
        UserMovieStatus userMovieStatus=userMovieStatusRepository.findByUser_IdAndMovie_Id(user_id, movie_id).orElseGet(() ->
        {UserMovieStatus temp = new UserMovieStatus();
            User user = userRepository.getById(user_id);
            Movie movie = movieRepository.getById(movie_id);
            temp.setUser(user);
            temp.setMovie(movie);
            temp.setRelation_type(RelationTypeEnum.LATER_WATCH);
            return temp;
        });
        userMovieStatus.setRelation_type(RelationTypeEnum.LATER_WATCH);
        userMovieStatusRepository.save(userMovieStatus);
    }


    //Test Fonksiyonu(db elle girmeyelim diye)
    public DtoUserMovieStatus setStatus(Long user_id, Long movie_id)
    {
        User user= userRepository.getOne(user_id);
        Movie movie=movieRepository.getOne(movie_id);
        UserMovieStatus userMovieStatus=new UserMovieStatus();
        userMovieStatus.setMovie(movie);
        userMovieStatus.setUser(user);
        userMovieStatus.setLiked(true);
        userMovieStatus.setRelation_type(RelationTypeEnum.LATER_WATCH);
        DtoUserMovieStatus dtoUserMovieStatus=new DtoUserMovieStatus();
        BeanUtils.copyProperties(userMovieStatus,dtoUserMovieStatus);
        userMovieStatusRepository.save(userMovieStatus);
        return dtoUserMovieStatus;
    }


    public List<DtoUserMovieStatus> getWatchedMovies(Long user_id)
    {
        List<DtoUserMovieStatus> watched_movie=userMovieStatusRepository.findByUserIdAndRelationTypeDto(user_id, RelationTypeEnum.WATCHED);
        if(watched_movie.isEmpty())
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Bu kullanıcının izlediği filmi yok",user_id));
        }
        return  watched_movie;
    }

    public List<DtoUserMovieStatus> getWatchLaterMovies(Long user_id)
    {
        List<DtoUserMovieStatus> watch_later_movie=userMovieStatusRepository.findByUserIdAndRelationTypeDto(user_id, RelationTypeEnum.LATER_WATCH);
        if(watch_later_movie.isEmpty())
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST," Bu kullanıcının izleyeceği film yok",user_id));
        }
        return  watch_later_movie;
    }


    public void deleteWatchLater(Long user_id,Long movie_id)
    {
        if(userMovieStatusRepository.existsByUser_IdAndMovie_IdAndRelation_type(user_id,movie_id,RelationTypeEnum.LATER_WATCH))
        {
            int deleted_movies_number= userMovieStatusRepository.deleteByUserAndMovieAndRelationType(user_id,movie_id,RelationTypeEnum.LATER_WATCH);
            if(deleted_movies_number==0)
            {
                throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Sonradan izlenecek filmi yok bu kişinin",user_id));
            }
        }
        else{throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Bu kişinin sonradan izleyeceği film bu değil zaten",user_id));}
    }


    public void deleteWatched(Long user_id,Long movie_id)
    {
        if(userMovieStatusRepository.existsByUser_IdAndMovie_IdAndRelation_type(user_id,movie_id,RelationTypeEnum.WATCHED))
        {
            int deleted_movies_number= userMovieStatusRepository.deleteByUserAndMovieAndRelationType(user_id,movie_id,RelationTypeEnum.WATCHED);
            if(deleted_movies_number==0)
            {
                throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," İzlediği filmi yok bu kişinin",user_id));
            }
        }
        else{throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Bu kişinin izlediği film bu değil zaten",user_id));}
    }
}
