package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IProfileController;
import com.deuceng.moviedive.dto.DtoUpdateUserRequest;
import com.deuceng.moviedive.dto.UserResponseDto;
import com.deuceng.moviedive.service.IProfileServices;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="rest/api/movie_dive/profile/")
public class ProfileControllerImpl implements IProfileController
{
    @Autowired
    private IProfileServices profileService;


    @PutMapping(path="update")
    public UserResponseDto updateUser(HttpSession session, @RequestBody DtoUpdateUserRequest request) {
        Long id = (Long) session.getAttribute("USER_ID");
        return profileService.updateProfile(id, request);
    }

    @GetMapping(path="get/me")
    public DtoUpdateUserRequest getMe(HttpSession session) {
        Long id = (Long) session.getAttribute("USER_ID");
        return profileService.getUser(id);
    }

    @GetMapping(path="getuser/{user_id}")
    public DtoUpdateUserRequest getUser(@PathVariable (name="user_id") Long user_id)
    {
        return profileService.getUser(user_id);
    }

    @GetMapping(path="getusername/{username}")
    public DtoUpdateUserRequest getUserByUsername(@PathVariable(name="username") String name)
    {
        return profileService.findByUsername(name);
    }
}
