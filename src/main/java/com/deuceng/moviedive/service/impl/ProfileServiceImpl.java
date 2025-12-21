package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import com.deuceng.moviedive.dto.UserResponseDto;
import com.deuceng.moviedive.entity.City;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.CityRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IProfileServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements IProfileServices {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CityRepository cityRepository;

    public RegisterResponse updateProfile(Long id, RegisterRequest request) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok", id)));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername());
        }
        if (request.getFirstname() != null) user.setFirstname(request.getFirstname());
        if (request.getSurname() != null) user.setSurname(request.getSurname());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(request.getPassword());
        if (request.getRole() != null)
        {
            user.setRole(request.getRole());
        }
        if(request.getCityId() !=null)
        {
            City city = cityRepository.findById(request.getCityId()).orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir city yok", id)));
            user.setCity(city);
        };
        userRepository.save(user);
        RegisterResponse dtoUpdateUserRequest = new RegisterResponse();
        BeanUtils.copyProperties(user, dtoUpdateUserRequest);
        dtoUpdateUserRequest.setCityName(user.getCity().getName());
        return dtoUpdateUserRequest;
    }

    public RegisterRequest getUser(Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok", id)));

        RegisterRequest dtoUpdateUserRequest = new RegisterRequest();
        BeanUtils.copyProperties(user, dtoUpdateUserRequest);
        dtoUpdateUserRequest.setCityId(user.getCity().getId());
        return dtoUpdateUserRequest;
    }

    public RegisterRequest findByUsername(String name)
    {
        User user = userRepository.findByUsername(name).
                orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok", name)));
        RegisterRequest dtoUpdateUserRequest = new RegisterRequest();
        BeanUtils.copyProperties(user, dtoUpdateUserRequest);
        dtoUpdateUserRequest.setCityId(user.getCity().getId());
        return dtoUpdateUserRequest;
    }
}