package com.deuceng.moviedive.service;

import com.deuceng.moviedive.dto.EventResponseDto;
import com.deuceng.moviedive.entity.Event;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface IEventService {

    Event createEvent(Event event, HttpSession session);

    List<EventResponseDto> getAllEventsWithActiveCount();

}
