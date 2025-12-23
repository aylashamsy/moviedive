package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.dto.DtoFollow;
import com.deuceng.moviedive.dto.DtoGetAllFollow;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.service.IFollowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="rest/api/movie_dive/relation/")
public class FollowControllerImpl
{
    @Autowired
    private IFollowService followService;

    @PostMapping(path="follow/{user_id}")
    public DtoFollow follow(HttpSession session,  @PathVariable (name="user_id") Long following_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        return followService.follow(user_id,following_id);
    }

    @PostMapping(path="unfollow/{user_id}")
    public DtoFollow unfollow(HttpSession session, @PathVariable (name="user_id") Long following_id)
    {
        Long user_id = (Long) session.getAttribute("USER_ID");
        if (user_id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, " Giriş yapmalısınız", null));
        }
        return followService.unfollow(user_id,following_id);
    }

    @GetMapping(path="isfollow/{user_id}/{follower_id}")
    public boolean isFollow(@PathVariable (name="user_id") Long user_id, @PathVariable (name="follower_id") Long following_id)
    {
        return followService.isFollow(user_id, following_id);
    }

    @GetMapping(path="get/followercount/{user_id}")
    public Long getFollowerCount(@PathVariable (name="user_id") Long user_id)
    {
        return followService.getFollowerCount(user_id);
    }

    @GetMapping(path="get/followingcount/{user_id}")
    public Long getFollowingCount(@PathVariable (name="user_id") Long user_id)
    {
        return followService.getFollowingCount(user_id);
    }

    @GetMapping(path="get/followers/{user_id}")
    public List<DtoGetAllFollow> getFollowers(@PathVariable Long user_id)
    {
        return followService.getFollowers(user_id);
    }

    @GetMapping(path="get/followings/{user_id}")
    public List<DtoGetAllFollow> getFollowings(@PathVariable Long user_id)
    {
        return followService.getFollowings(user_id);
    }
}
