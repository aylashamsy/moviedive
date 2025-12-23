package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.controller.IEventController;
import com.deuceng.moviedive.dto.EventCreateRequest;
import com.deuceng.moviedive.dto.EventParticipantDto;
import com.deuceng.moviedive.dto.EventResponse;
import com.deuceng.moviedive.service.IEventService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.deuceng.moviedive.dto.EventUpdateRequest;

@RestController
@RequestMapping(path = "rest/api/movie_dive/events/")
@RequiredArgsConstructor
public class EventControllerImpl implements IEventController {

    private final IEventService eventService;

    @Override
    @PostMapping
    public EventResponse create(@RequestBody EventCreateRequest request, HttpSession session) {
        return eventService.createEvent(request, session);
    }

    @Override
    @GetMapping
    public List<EventResponse> listUpcoming() {
        return eventService.listUpcomingEvents();
    }

    @Override
    @GetMapping(path = "{id}")
    public EventResponse get(@PathVariable(name = "id") Long id) {
        return eventService.getEventDetail(id);
    }

    @Override
    @PostMapping(path = "{id}/register")
    public String register(@PathVariable(name = "id") Long id, HttpSession session) {
        return eventService.registerToEvent(id, session);
    }

    @Override
    @GetMapping(path = "{eventId}/participants")
    public List<EventParticipantDto> participants(@PathVariable(name="eventId") Long eventId) {
        return eventService.getEventParticipants(eventId);
    }

    @Override
    @PutMapping(path = "{id}")
    public EventResponse update(@PathVariable(name = "id") Long id,
                                @RequestBody EventUpdateRequest request,
                                HttpSession session) {
        return eventService.updateEvent(id, request, session);
    }

}
