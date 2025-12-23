package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Upcoming (home page)
    List<Event> findByEventDateTimeAfterOrderByEventDateTimeAsc(LocalDateTime now);


    // Search bar: event TITLE’a göre upcoming
    List<Event> findByTitleContainingIgnoreCaseAndEventDateTimeAfter(String title, LocalDateTime now);

}
