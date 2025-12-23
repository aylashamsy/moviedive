package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.EventCreateRequest;
import com.deuceng.moviedive.dto.EventParticipantDto;
import com.deuceng.moviedive.dto.EventResponse;
import com.deuceng.moviedive.dto.EventUpdateRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface IEventService {

    EventResponse createEvent(EventCreateRequest request, HttpSession session);

    List<EventResponse> listUpcomingEvents();

    EventResponse getEventDetail(Long id);

    List<EventParticipantDto> getEventParticipants(Long eventId);

    EventResponse updateEvent(Long id, EventUpdateRequest request, HttpSession session);

    String registerToEvent(Long eventId, HttpSession session);

    List<EventResponse> getMyRegisteredEvents(HttpSession session);


}
