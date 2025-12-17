package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.EventResponseDto;
import com.deuceng.moviedive.dto.RegisterRequestDto;
import com.deuceng.moviedive.dto.RegistrationResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IEventRegistrationController {

    ResponseEntity<EventResponseDto> getEvent(Long eventId);

    ResponseEntity<RegistrationResponseDto> register(Long eventId, RegisterRequestDto req);

    ResponseEntity<RegistrationResponseDto> cancel(Long eventId, RegisterRequestDto req);

    ResponseEntity<Long> activeCount(Long eventId);

    ResponseEntity<List<RegistrationResponseDto>> list(Long eventId);
}
