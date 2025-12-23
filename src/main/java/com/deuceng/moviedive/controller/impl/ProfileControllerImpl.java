package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IProfileController;
import com.deuceng.moviedive.dto.PublicUserProfileDto;
import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import com.deuceng.moviedive.service.IProfileServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.deuceng.moviedive.dto.ChangePasswordRequest;

@RestController
@RequestMapping(path="rest/api/movie_dive/profile/")
public class ProfileControllerImpl implements IProfileController
{
    @Autowired
    private IProfileServices profileService;


    @PutMapping(path="update")
    public RegisterResponse updateUser(HttpSession session, @RequestBody RegisterRequest request) {
        Long id = (Long) session.getAttribute("USER_ID");
        return profileService.updateProfile(id, request);
    }

    @GetMapping(path="get/me")
    public RegisterResponse getMe(HttpSession session) {
        Long id = (Long) session.getAttribute("USER_ID");
        return profileService.getUser(id);
    }

    @GetMapping(path="getuser/{user_id}")
    public RegisterResponse getUser(@PathVariable (name="user_id") Long user_id)
    {
        return profileService.getUser(user_id);
    }

    @GetMapping(path="getusername/{username}")
    public PublicUserProfileDto getUserByUsername(@PathVariable(name="username") String name)
    {
        return profileService.findByUsername(name);
    }


    @PutMapping(path="change-password")
    public RegisterResponse changePassword(HttpSession session,
                                           @RequestBody ChangePasswordRequest request) {
        Long id = (Long) session.getAttribute("USER_ID");
        return profileService.changePassword(id, request);
    }

}
