package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.EventResponse;
import com.deuceng.moviedive.dto.MovieDto;
import com.deuceng.moviedive.dto.SearchResponse;
import com.deuceng.moviedive.entity.Event;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.repository.EventRepository;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.repository.EventRegistrationRepository;
import com.deuceng.moviedive.service.ISearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements ISearchService {

    private final MovieRepository movieRepository;
    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;

    @Override
    public SearchResponse search(String q) {
        if (q == null || q.isBlank()) {
            return new SearchResponse(List.of(), List.of());
        }

        // movies
        List<MovieDto> movies = movieRepository.findByTitleContainingIgnoreCase(q)
                .stream()
                .map(this::toMovieDto)
                .toList();

        // events (only upcoming)
        List<EventResponse> events = eventRepository
                .findByTitleContainingIgnoreCaseAndEventDateTimeAfter(q, LocalDateTime.now())
                .stream()
                .map(this::toEventResponse)
                .toList();


        return new SearchResponse(movies, events);
    }

    private MovieDto toMovieDto(Movie movie) {
        MovieDto dto = new MovieDto();

        dto.setTmdbId(movie.getTmdbId());

        dto.setTitle(movie.getTitle());
        dto.setOverview(movie.getOverview());


        dto.setRelease_date(movie.getRelease_date());
        dto.setVote_average(movie.getVote_average());

        dto.setPoster_url(movie.getPoster_path());

        dto.setVote_count(movie.getVote_count());

        if (movie.getGenres() != null) {
            dto.setGenre_names(
                    movie.getGenres().stream()
                            .map(g -> g.getName())
                            .toList()
            );
        }

        if (movie.getDirectors() != null) {
            dto.setDirector_names(
                    movie.getDirectors().stream()
                            .map(d -> d.getName())
                            .toList()
            );
        }

        if (movie.getCastMembers() != null) {
            dto.setActors(
                    movie.getCastMembers().stream()
                            .map(c -> c.getName())
                            .toList()
            );
        }

        return dto;
    }


    private EventResponse toEventResponse(Event event) {
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

        r.setRegisteredCount(eventRegistrationRepository.countByEvent_Id(event.getId()));
        return r;
    }
}