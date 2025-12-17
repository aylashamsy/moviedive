package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.EventResponseDto;
import com.deuceng.moviedive.dto.RegistrationResponseDto;
import com.deuceng.moviedive.entity.Event;
import com.deuceng.moviedive.entity.EventRegistration;
import com.deuceng.moviedive.entity.RegistrationStatus;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.repository.EventRegistrationRepository;
import com.deuceng.moviedive.repository.EventRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IEventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventRegistrationServiceImpl implements IEventRegistrationService {

    @Autowired
    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    @Transactional
    public RegistrationResponseDto register(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        EventRegistration reg = registrationRepository
                .findByEvent_EventIdAndUser_Id(eventId, userId)
                .orElse(null);

        if (reg == null) {
            reg = EventRegistration.builder()
                    .event(event)
                    .user(user)
                    .status(RegistrationStatus.ACTIVE)
                    .build();
        } else {
            // Daha önce CANCELED olduysa tekrar aktive et
            reg.setStatus(RegistrationStatus.ACTIVE);
            reg.setCanceledAt(null);
        }

        EventRegistration saved = registrationRepository.save(reg);
        return toRegistrationDto(saved);
    }

    @Transactional
    public RegistrationResponseDto cancel(Long eventId, Long userId) {
        EventRegistration reg = registrationRepository
                .findByEvent_EventIdAndUser_Id(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Registration not found for eventId=" + eventId + ", userId=" + userId
                ));

        reg.setStatus(RegistrationStatus.CANCELED);
        reg.setCanceledAt(LocalDateTime.now());

        return toRegistrationDto(reg);
    }

    @Transactional(readOnly = true)
    public long activeCount(Long eventId) {
        // İstersen exists check kaldırılabilir, ama hata mesajı daha temiz olur.
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found: " + eventId);
        }
        return registrationRepository.countByEvent_EventIdAndStatus(eventId, RegistrationStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponseDto> registrationsOfEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event not found: " + eventId);
        }

        return registrationRepository.findAllByEvent_EventId(eventId)
                .stream()
                .map(this::toRegistrationDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponseDto getEventWithCount(Long eventId) {
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        long count = registrationRepository.countByEvent_EventIdAndStatus(eventId, RegistrationStatus.ACTIVE);

        EventResponseDto dto = new EventResponseDto();
        dto.setEventId(e.getEventId());
        dto.setEventName(e.getEventName());
        dto.setCountry(e.getCountry());
        dto.setCity(e.getCity());
        dto.setAddressLine(e.getAddressLine());
        dto.setEventDate(e.getEventDate());
        dto.setCreationTime(e.getCreationTime());

        // Movie PK alanın movieId değilse düzelt
        dto.setMovieId(e.getMovie().getId());

        // User PK alanın id değilse düzelt
        dto.setCreatorUserId(e.getCreator().getId());

        dto.setActiveRegistrationCount(count);

        return dto;
    }

    private RegistrationResponseDto toRegistrationDto(EventRegistration r) {
        RegistrationResponseDto dto = new RegistrationResponseDto();
        dto.setId(r.getId());
        dto.setEventId(r.getEvent().getEventId());

        // User PK alanın id değilse düzelt
        dto.setUserId(r.getUser().getId());

        dto.setStatus(r.getStatus());
        dto.setRegisteredAt(r.getRegisteredAt());
        dto.setCanceledAt(r.getCanceledAt());
        return dto;
    }
}
