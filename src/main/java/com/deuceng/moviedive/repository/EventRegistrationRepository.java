package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEvent_IdAndUser_Id(Long eventId, Long userId);

    long countByEvent_Id(Long eventId);

    List<EventRegistration> findByEvent_Id(Long eventId);
}
