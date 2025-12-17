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
@RequestMapping("/api/events")
public class EventRegistrationControllerImpl {

    @Autowired
    private final IEventRegistrationService service;

    // Event detay + ACTIVE kayıt sayısı
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.getEventWithCount(eventId));
    }

    // Register
    @PostMapping("/{eventId}/register")
    public ResponseEntity<RegistrationResponseDto> register(
            @PathVariable Long eventId,
            @RequestBody RegisterRequestDto req
    ) {
        return ResponseEntity.ok(service.register(eventId, req.getUserId()));
    }

    // Cancel
    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<RegistrationResponseDto> cancel(
            @PathVariable Long eventId,
            @RequestBody RegisterRequestDto req
    ) {
        return ResponseEntity.ok(service.cancel(eventId, req.getUserId()));
    }

    // ACTIVE kayıt sayısı
    @GetMapping("/{eventId}/registrations/count")
    public ResponseEntity<Long> activeCount(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.activeCount(eventId));
    }

    // Kayıt listesi (ACTIVE + CANCELED)
    @GetMapping("/{eventId}/registrations")
    public ResponseEntity<List<RegistrationResponseDto>> list(@PathVariable Long eventId) {
        return ResponseEntity.ok(service.registrationsOfEvent(eventId));
    }
}
