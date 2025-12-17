package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
