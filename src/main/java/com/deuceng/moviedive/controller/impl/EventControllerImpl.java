package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.entity.Event;
import com.deuceng.moviedive.service.IEventService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/api/events")
public class EventControllerImpl {

    @Autowired
    private final IEventService eventService;

    @PostMapping(path="/create_event")
    public ResponseEntity<Event> createEvent(
            @RequestBody Event event,
            HttpSession session
    ) {
        return ResponseEntity.ok(eventService.createEvent(event, session));
    }
}
