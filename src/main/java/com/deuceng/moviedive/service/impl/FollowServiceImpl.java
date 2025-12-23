package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.DtoFollow;
import com.deuceng.moviedive.dto.DtoGetAllFollow;
import com.deuceng.moviedive.entity.Follow;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.FollowRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IFollowService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements IFollowService
{
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public DtoFollow follow(Long follower_id, Long following_id)
    {
        User user=userRepository.findById(following_id).orElseThrow(()->new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok.", following_id)));
        User own=userRepository.findById(follower_id).orElseThrow(()->new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok.", follower_id)));
        if(follower_id==following_id)
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Kendini takip edemezsin", follower_id));
        }
        if(followRepository.existsByFollower_IdAndFollowing_Id(following_id, follower_id))
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Bu kişi daha önceden takip ettiğiniz biri", user.getUsername()));
        }
        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowing(own);
        followRepository.save(follow);

        DtoFollow dtoFoloow=new DtoFollow();
        dtoFoloow.setFollower(user);
        dtoFoloow.setFollowing(own);
        return dtoFoloow;
    }


    @Transactional
    @Override
    public DtoFollow unfollow(Long follower_id, Long following_id) {
        User user=userRepository.findById(following_id).orElseThrow(()->new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok.", follower_id)));
        User own=userRepository.findById(follower_id).orElseThrow(()->new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok.", following_id)));
        DtoFollow dtoFollow=new DtoFollow();
        dtoFollow.setFollower(user);
        dtoFollow.setFollowing(own);
        int deleted_relations=followRepository.deleteByFollowerAndFollowing(following_id,follower_id);
        if(deleted_relations==0)
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Bu kişiyi zaten takip etmiyorsun.", following_id));
        }
        return dtoFollow;
    }

    @Override
    public boolean isFollow(Long follower_id, Long following_id) {
        return followRepository.existsByFollower_IdAndFollowing_Id(follower_id , following_id);
    }

    @Override
    public Long getFollowerCount(Long user_id) {
        return followRepository.countByFollower_Id(user_id);
    }

    @Override
    public Long getFollowingCount(Long user_id) {
        return followRepository.countByFollowing_Id(user_id);
    }

    @Override
    public List<DtoGetAllFollow> getFollowers(Long follower_id) {
        List<User> followers = followRepository.findFollowersAsUsers(follower_id);
        if(followers.isEmpty())
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Bu kişinin takipçisi yok", follower_id));
        }
        List<DtoGetAllFollow> dtoGetAllFollow=new ArrayList<>();
        for(int i =0;i<followers.size();i++)
        {
            DtoGetAllFollow dto=new DtoGetAllFollow();
            dto.setId(followers.get(i).getId());
            dto.setUsername(followers.get(i).getUsername());
            dto.setFirstname(followers.get(i).getFirstname());
            dto.setSurname(followers.get(i).getSurname());
            dtoGetAllFollow.add(dto);
        }
        return dtoGetAllFollow;
    }

    @Override
    public List<DtoGetAllFollow> getFollowings(Long following_id) {
        List<User> followings = followRepository.findFollowingsAsUsers(following_id);
        if(followings.isEmpty())
        {
            throw new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Bu kişinin takip ettiği bir kişi yok", following_id));
        }
        List<DtoGetAllFollow> dtoGetAllFollow=new ArrayList<>();
        for(int i =0;i<followings.size();i++)
        {
            DtoGetAllFollow dto=new DtoGetAllFollow();
            dto.setId(followings.get(i).getId());
            dto.setUsername(followings.get(i).getUsername());
            dto.setFirstname(followings.get(i).getFirstname());
            dto.setSurname(followings.get(i).getSurname());
            dtoGetAllFollow.add(dto);
        }
        return dtoGetAllFollow;
    }
}
