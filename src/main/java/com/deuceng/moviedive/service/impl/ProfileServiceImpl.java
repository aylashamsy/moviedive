package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.ChangePasswordRequest;
import com.deuceng.moviedive.dto.PublicUserProfileDto;
import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
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

    public RegisterResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage<>(MessageType.NO_RECORD_EXIST, "Böyle bir kullanıcı yok", id)
                ));

        RegisterResponse dto = new RegisterResponse();
        BeanUtils.copyProperties(user, dto);

        if (user.getCity() != null) {
            dto.setCityName(user.getCity().getName());
        } else {
            dto.setCityName(null);
            dto.setCityName(null);
        }



        return dto;
    }


    public PublicUserProfileDto findByUsername(String name)
    {
        User user = userRepository.findByUsername(name).
                orElseThrow(() -> new BaseException(new ErrorMessage<>(MessageType.NO_RECORD_EXIST, " Böyle bir kullanıcı yok", name)));
        PublicUserProfileDto PublicUserProfileDto = new PublicUserProfileDto();
        BeanUtils.copyProperties(user, PublicUserProfileDto);
        PublicUserProfileDto.setCityName(user.getCity().getName());
        return PublicUserProfileDto;
    }
    @Override
    public RegisterResponse changePassword(Long id, ChangePasswordRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage<>(MessageType.NO_RECORD_EXIST,
                                "Böyle bir kullanıcı yok", id)
                ));

        // 🔒 Null / boş kontrol
        if (request == null ||
                request.getOldPassword() == null ||
                request.getNewPassword() == null ||
                request.getConfirmPassword() == null) {

            throw new BaseException(
                    new ErrorMessage<>(MessageType.GENERAL_EXCEPTION,
                            "Şifre alanları boş olamaz", null)
            );
        }

        // 🔒 eski şifre doğru mu?
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new BaseException(
                    new ErrorMessage<>(MessageType.GENERAL_EXCEPTION,
                            "Eski şifre yanlış", null)
            );
        }

        // 🔒 yeni şifreler eşleşiyor mu?
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BaseException(
                    new ErrorMessage<>(MessageType.GENERAL_EXCEPTION,
                            "Yeni şifreler uyuşmuyor", null)
            );
        }


        // ✅ şifreyi değiştir (plain text)
        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        RegisterResponse dto = new RegisterResponse();
        BeanUtils.copyProperties(user, dto);

        if (user.getCity() != null) {
            dto.setCityName(user.getCity().getName());
        }

        return dto;
    }
}