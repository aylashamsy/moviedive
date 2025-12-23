package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.DtoFollow;
import com.deuceng.moviedive.dto.DtoGetAllFollow;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface IFollowController
{
    public DtoFollow follow(HttpSession session,  @PathVariable (name="user_id") Long follower_id);

    public DtoFollow unfollow(HttpSession session, @PathVariable (name="user_id") Long follower_id);

    public boolean isFollow(@PathVariable (name="user_id") Long user_id, @PathVariable (name="follower_id") Long follower_id);
    public Long getFollowerCount(@PathVariable (name="user_id") Long user_id);

    public Long getFollowingCount(@PathVariable(name="user_id") Long user_id);
    public List<DtoGetAllFollow> getFollowers(Long user_id);
    public List<DtoGetAllFollow> getFollowings(Long user_id);
}
