package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.EventResponseDto;
import com.deuceng.moviedive.dto.RegistrationResponseDto;

import java.util.List;

public interface IEventRegistrationService {

    RegistrationResponseDto register(Long eventId, Long userId);

    RegistrationResponseDto cancel(Long eventId, Long userId);

    long activeCount(Long eventId);

    List<RegistrationResponseDto> registrationsOfEvent(Long eventId);

    EventResponseDto getEventWithCount(Long eventId);
}
