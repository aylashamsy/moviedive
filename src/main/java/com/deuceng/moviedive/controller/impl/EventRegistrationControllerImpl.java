package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.dto.EventResponseDto;
import com.deuceng.moviedive.dto.RegisterRequestDto;
import com.deuceng.moviedive.dto.RegistrationResponseDto;
import com.deuceng.moviedive.service.IEventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path ="/rest/api/event_register")
public class EventRegistrationControllerImpl {

    @Autowired
    private final IEventRegistrationService service;

    // Event detay + ACTIVE kayıt sayısı
    @GetMapping(path ="/getevent/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getEventWithCount(eventId));
    }

    // Register
    @PostMapping(path ="/register/{eventId}")
    public ResponseEntity<RegistrationResponseDto> register(
            @PathVariable Long eventId,
            @RequestBody RegisterRequestDto req
    ) {
        return ResponseEntity.ok(service.register(eventId, req.getUserId()));
    }

    // Cancel
    @PostMapping(path ="/cancel/{eventId}")
    public ResponseEntity<RegistrationResponseDto> cancel(
            @PathVariable Long eventId,
            @RequestBody RegisterRequestDto req
    ) {
        return ResponseEntity.ok(service.cancel(eventId, req.getUserId()));
    }

    // ACTIVE kayıt sayısı
    @GetMapping(path ="/registrations/count/{eventId}")
    public ResponseEntity<Long> activeCount(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.activeCount(eventId));
    }

    // Kayıt listesi (ACTIVE + CANCELED)
    @GetMapping(path ="/registrations/{eventId}")
    public ResponseEntity<List<RegistrationResponseDto>> list(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.registrationsOfEvent(eventId));
    }
}
