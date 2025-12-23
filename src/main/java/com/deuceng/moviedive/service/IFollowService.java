package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.DtoFollow;
import com.deuceng.moviedive.dto.DtoGetAllFollow;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IFollowService
{
    public DtoFollow follow(Long user_id,Long follower_id);

    public DtoFollow unfollow(Long user_id, Long follower_id);

    public boolean isFollow(Long user_id,Long follower_id);
    public Long getFollowerCount(Long user_id);

    public Long getFollowingCount(Long user_id);
    public List<DtoGetAllFollow> getFollowers(Long user_id);
    public List<DtoGetAllFollow> getFollowings(Long user_id);
}
