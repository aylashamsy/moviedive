package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.DtoFollow;
import com.deuceng.moviedive.repository.FollowRepository;
import com.deuceng.moviedive.service.IFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements IFollowService
{
    @Autowired
    private FollowRepository followRepository;


    @Override
    public DtoFollow follow(Long follower_id) {
        return null;
    }

    @Override
    public DtoFollow unfollow(Long follower_id) {
        return null;
    }

    @Override
    public boolean isFollow(Long user_id) {
        return false;
    }

    @Override
    public Long getFollowerCount(Long user_id) {
        return 0L;
    }

    @Override
    public Long getFollowingCount(Long user_id) {
        return 0L;
    }
}
