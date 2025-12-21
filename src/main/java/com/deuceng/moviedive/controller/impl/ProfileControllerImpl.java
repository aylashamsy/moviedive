package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IProfileController;
import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
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
    public RegisterResponse updateUser(HttpSession session, @RequestBody RegisterRequest request) {
        Long id = (Long) session.getAttribute("USER_ID");
        return profileService.updateProfile(id, request);
    }

    @GetMapping(path="get/me")
    public RegisterRequest getMe(HttpSession session) {
        Long id = (Long) session.getAttribute("USER_ID");
        return profileService.getUser(id);
    }

    @GetMapping(path="getuser/{user_id}")
    public RegisterRequest getUser(@PathVariable (name="user_id") Long user_id)
    {
        return profileService.getUser(user_id);
    }

    @GetMapping(path="getusername/{username}")
    public RegisterRequest getUserByUsername(@PathVariable(name="username") String name)
    {
        return profileService.findByUsername(name);
    }
}
