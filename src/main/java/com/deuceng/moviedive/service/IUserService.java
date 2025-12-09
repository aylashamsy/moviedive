package com.deuceng.moviedive.service;

import com.deuceng.moviedive.entity.User;

import java.util.List;

public interface IUserService {

    User saveUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();
}
