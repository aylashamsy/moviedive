package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.DtoFollow;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

public interface IFollowService
{
    public DtoFollow follow(Long follower_id);

    public DtoFollow unfollow(Long follower_id);

    public boolean isFollow(Long user_id);
    public Long getFollowerCount(Long user_id);

    public Long getFollowingCount(Long user_id);
}
