package com.deuceng.moviedive.service;

import com.deuceng.moviedive.entity.Event;
import jakarta.servlet.http.HttpSession;

public interface IEventService {

    Event createEvent(Event event, HttpSession session);
}
