package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.EventCreateRequest;
import com.deuceng.moviedive.dto.EventParticipantDto;
import com.deuceng.moviedive.dto.EventResponse;
import com.deuceng.moviedive.entity.*;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.*;
import com.deuceng.moviedive.service.IEventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deuceng.moviedive.dto.EventUpdateRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class EventServiceImpl implements IEventService {

    @Autowired private EventRepository eventRepository;
    @Autowired private EventRegistrationRepository eventRegistrationRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private MovieRepository movieRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public EventResponse createEvent(EventCreateRequest request, HttpSession session) {
        requireVipOrAdmin(session);

        if (request == null) {
            throw new BaseException(new ErrorMessage(MessageType.INVALID_REQUEST, "İstek gövdesi boş", null));
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BaseException(new ErrorMessage(MessageType.MISSING_PARAMETER, "title zorunlu", request));
        }
        if (request.getLocationName() == null || request.getLocationName().isBlank()) {
            throw new BaseException(new ErrorMessage(MessageType.MISSING_PARAMETER, "locationName zorunlu", request));
        }
        if (request.getCityId() == null) {
            throw new BaseException(new ErrorMessage(MessageType.MISSING_PARAMETER, "cityId zorunlu", request));
        }
        if (request.getEventDateTime() == null || request.getEventDateTime().isBlank()) {
            throw new BaseException(new ErrorMessage(MessageType.MISSING_PARAMETER, "eventDateTime zorunlu", request));
        }

        LocalDateTime eventDateTime;
        try {
            eventDateTime = LocalDateTime.parse(request.getEventDateTime());
        } catch (DateTimeParseException ex) {
            throw new BaseException(new ErrorMessage(
                    MessageType.DATE_FORMAT_ERROR,
                    "eventDateTime ISO formatında olmalı. Örn: 2025-12-22T20:00",
                    request.getEventDateTime()
            ));
        }

        Long creatorId = (Long) session.getAttribute("USER_ID");
        City city = cityRepository.findById(request.getCityId()).orElseThrow(() ->
                new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Şehir bulunamadı", request.getCityId())));

        User creator = userRepository.findById(creatorId).orElseThrow(() ->
                new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Kullanıcı bulunamadı", creatorId)));

        Movie conceptMovie = null;
        if (request.getMovieId() != null) {
            conceptMovie = movieRepository.findById(request.getMovieId()).orElseThrow(() ->
                    new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Film bulunamadı", request.getMovieId())));
        }

        Event event = new Event();
        event.setTitle(request.getTitle().trim());
        event.setDescription(request.getDescription());
        event.setLocationName(request.getLocationName().trim());
        event.setCity(city);
        event.setConceptMovie(conceptMovie);
        event.setEventDateTime(eventDateTime);
        event.setCreatedBy(creator);
        event.setCreatedAt(LocalDateTime.now());

        return toResponse(eventRepository.save(event));
    }

    @Override
    public List<EventResponse> listUpcomingEvents() {
        return eventRepository
                .findByEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime.now())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EventResponse getEventDetail(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Event bulunamadı", id)));
        return toResponse(event);
    }

    @Override
    public String registerToEvent(Long eventId, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Giriş yapmalısınız", null));
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Event bulunamadı", eventId)));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Kullanıcı bulunamadı", userId)));

        if (eventRegistrationRepository.findByEvent_IdAndUser_Id(eventId, userId).isPresent()) {
            return "Zaten kayıtlısınız";
        }

        EventRegistration reg = new EventRegistration();
        reg.setEvent(event);
        reg.setUser(user);
        reg.setRegisteredAt(LocalDateTime.now());
        eventRegistrationRepository.save(reg);

        return "Kayıt başarılı";
    }

    private void requireVipOrAdmin(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        Role role = (Role) session.getAttribute("ROLE");

        if (userId == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Giriş yapmalısınız", null));
        }
        if (role == null || !(role == Role.VIP || role == Role.ADMIN)) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "VIP veya ADMIN gerekli", role));
        }
    }

    private EventResponse toResponse(Event event) {
        EventResponse r = new EventResponse();
        r.setId(event.getId());
        r.setTitle(event.getTitle());
        r.setDescription(event.getDescription());
        r.setLocationName(event.getLocationName());

        if (event.getCity() != null) {
            r.setCityId(event.getCity().getId());
            r.setCityName(event.getCity().getName());
        }

        if (event.getConceptMovie() != null) {
            r.setMovieId(event.getConceptMovie().getId());
            r.setMovieTitle(event.getConceptMovie().getTitle());
        }

        r.setEventDateTime(event.getEventDateTime() != null ? event.getEventDateTime().toString() : null);

        if (event.getCreatedBy() != null) {
            r.setCreatedByUserId(event.getCreatedBy().getId());
            r.setCreatedByUsername(event.getCreatedBy().getUsername());
        }

        r.setRegisteredCount(event.getId() == null ? 0 : eventRegistrationRepository.countByEvent_Id(event.getId()));
        return r;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventParticipantDto> getEventParticipants(Long eventId) {

        // event var mı kontrol etmek istersen:
        eventRepository.findById(eventId)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "Event bulunamadı", eventId)));

        return eventRegistrationRepository.findByEvent_Id(eventId)
                .stream()
                .map(er -> toParticipantDto(er.getUser()))
                .toList();
    }

    private EventParticipantDto toParticipantDto(User u) {
        if (u == null) return null;

        return new EventParticipantDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getFirstname(),
                u.getSurname(),
                u.getRole() != null ? u.getRole().name() : null
        );
    }

    @Override
    public EventResponse updateEvent(Long id, EventUpdateRequest request, HttpSession session) {
        requireVipOrAdmin(session);

        Event event = eventRepository.findById(id).orElseThrow(() ->
                new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Event bulunamadı", id)));

        // İstersen sadece oluşturan kişi güncellesin:
        Long userId = (Long) session.getAttribute("USER_ID");
        if (event.getCreatedBy() != null && event.getCreatedBy().getId() != null
                && !event.getCreatedBy().getId().equals(userId)) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Sadece event sahibi güncelleyebilir", null));
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocationName() != null && !request.getLocationName().isBlank()) {
            event.setLocationName(request.getLocationName().trim());
        }
        if (request.getCityId() != null) {
            City city = cityRepository.findById(request.getCityId()).orElseThrow(() ->
                    new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Şehir bulunamadı", request.getCityId())));
            event.setCity(city);
        }
        if (request.getMovieId() != null) {
            Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(() ->
                    new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Film bulunamadı", request.getMovieId())));
            event.setConceptMovie(movie);
        }
        if (request.getEventDateTime() != null && !request.getEventDateTime().isBlank()) {
            try {
                event.setEventDateTime(LocalDateTime.parse(request.getEventDateTime()));
            } catch (Exception ex) {
                throw new BaseException(new ErrorMessage(
                        MessageType.DATE_FORMAT_ERROR,
                        "eventDateTime ISO formatında olmalı. Örn: 2025-12-22T20:00",
                        request.getEventDateTime()
                ));
            }
        }

        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

}
