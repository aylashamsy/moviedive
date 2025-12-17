package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.EventRegistration;
import com.deuceng.moviedive.entity.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEvent_EventIdAndUser_Id(Long eventId, Long userId);

    long countByEvent_EventIdAndStatus(Long eventId, RegistrationStatus status);

    List<EventRegistration> findAllByEvent_EventId(Long eventId);
}
