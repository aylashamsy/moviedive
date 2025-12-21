package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.DtoUpdateUserRequest;
import com.deuceng.moviedive.dto.UserResponseDto;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IProfileServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements IProfileServices {
    @Autowired
    private UserRepository userRepository;

    public UserResponseDto updateProfile(Long id, DtoUpdateUserRequest request) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok", id)));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getFirstname() != null) user.setFirstname(request.getFirstname());
        if (request.getSurname() != null) user.setSurname(request.getSurname());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        //if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPassword() != null) user.setPassword(request.getPassword());
        if (request.getRole() != null)
        {
            user.setRole(request.getRole());
        }
        userRepository.save(user);
        UserResponseDto dtoUpdateUserRequest = new UserResponseDto();
        BeanUtils.copyProperties(user, dtoUpdateUserRequest);
        return dtoUpdateUserRequest;
    }

    public DtoUpdateUserRequest getUser(Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok", id)));
        DtoUpdateUserRequest dtoUpdateUserRequest = new DtoUpdateUserRequest();
        BeanUtils.copyProperties(user, dtoUpdateUserRequest);
        return dtoUpdateUserRequest;
    }

    public DtoUpdateUserRequest findByUsername(String name)
    {
        User user = userRepository.findByUsername(name).
                orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok", name)));
        DtoUpdateUserRequest dtoUpdateUserRequest = new DtoUpdateUserRequest();
        BeanUtils.copyProperties(user, dtoUpdateUserRequest);
        return dtoUpdateUserRequest;
    }
}