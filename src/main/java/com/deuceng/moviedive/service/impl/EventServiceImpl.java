package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.EventResponseDto;
import com.deuceng.moviedive.entity.Event;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.RegistrationStatus;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.EventRegistrationRepository;
import com.deuceng.moviedive.repository.EventRepository;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IEventService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    @Autowired
    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final SessionAuthService sessionAuthService;

    @Override
    @Transactional
    public Event createEvent(Event event, HttpSession session) {

        // 1️⃣ VIP kontrolü (EN ÖNEMLİ KURAL)
        sessionAuthService.requireVip(session);

        // 2️⃣ Login olmuş kullanıcı
        Long creatorId = sessionAuthService.requireUserId(session);
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "User not found", creatorId)
                ));

        // 3️⃣ Movie kontrolü (event içinden geliyor)
        Movie movie = movieRepository.findById(event.getMovie().getId())
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "Movie not found", event.getMovie().getId())
                ));

        // 4️⃣ Güvenli alan setleri
        event.setCreator(creator);
        event.setMovie(movie);

        // creationTime zaten @PrePersist ile setleniyor

        return eventRepository.save(event);
    }
    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllEventsWithActiveCount() {
        List<Event> events = eventRepository.findAll(); // istersen date'e göre sıralatırız

        return events.stream()
                .map(e -> {
                    Long activeCount = eventRegistrationRepository
                            .countByEvent_EventIdAndStatus(e.getEventId(), RegistrationStatus.ACTIVE);

                    return new EventResponseDto(
                            e.getEventId(),                 // eventId
                            e.getEventName(),          // eventName
                            e.getCountry(),            // country
                            e.getCity(),               // city
                            e.getAddressLine(),        // addressLine
                            e.getEventDate(),          // eventDate
                            e.getCreationTime(),       // creationTime
                            e.getMovie().getId(),            // movieId
                            e.getCreator().getId(),      // creatorUserId
                            activeCount                // activeRegistrationCount
                    );
                })
                .toList();
    }

}
