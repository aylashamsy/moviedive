package com.deuceng.moviedive.controller;

import com.deuceng.moviedive.dto.EventCreateRequest;
import com.deuceng.moviedive.dto.EventParticipantDto;
import com.deuceng.moviedive.dto.EventResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IEventController {
    EventResponse create(@RequestBody EventCreateRequest request, HttpSession session);
    List<EventResponse> listUpcoming();
    EventResponse get(@PathVariable(name = "id") Long id);
    String register(@PathVariable(name = "id") Long id, HttpSession session);
    List<EventParticipantDto> participants(@PathVariable(name="eventId") Long eventId);
    EventResponse update(@PathVariable(name = "id") Long id, @RequestBody com.deuceng.moviedive.dto.EventUpdateRequest request, HttpSession session);
}

