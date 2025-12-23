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
    @PostMapping(path ="/create")
    public EventResponse create(@RequestBody EventCreateRequest request, HttpSession session) {
        return eventService.createEvent(request, session);
    }

    @Override
    @GetMapping(path ="/upcoming")
    public List<EventResponse> listUpcoming() {
        return eventService.listUpcomingEvents();
    }

    @Override
    @GetMapping(path = "/getevent/{id}")
    public EventResponse get(@PathVariable(name = "id") Long id) {
        return eventService.getEventDetail(id);
    }

    @Override
    @PostMapping(path = "/register/{id}")
    public String register(@PathVariable(name = "id") Long id, HttpSession session) {
        return eventService.registerToEvent(id, session);
    }

    @Override
    @GetMapping(path = "/participants/{eventId}")
    public List<EventParticipantDto> participants(@PathVariable(name="eventId") Long eventId) {
        return eventService.getEventParticipants(eventId);
    }

    @Override
    @PutMapping(path = "/update-event/{id}")
    public EventResponse update(@PathVariable(name = "id") Long id,
                                @RequestBody EventUpdateRequest request,
                                HttpSession session) {
        return eventService.updateEvent(id, request, session);
    }
    @GetMapping("/my-registered")
    public List<EventResponse> myRegistered(HttpSession session) {
        return eventService.getMyRegisteredEvents(session);
    }

}
