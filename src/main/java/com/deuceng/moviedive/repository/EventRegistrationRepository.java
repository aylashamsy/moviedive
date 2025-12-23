package com.deuceng.moviedive.repository;

import com.deuceng.moviedive.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEvent_IdAndUser_Id(Long eventId, Long userId);

    long countByEvent_Id(Long eventId);

    List<EventRegistration> findByEvent_Id(Long eventId);
    @Query("""
        select er
        from EventRegistration er
        join fetch er.event e
        left join fetch e.city c
        left join fetch e.conceptMovie m
        join fetch er.user u
        where u.id = :userId
        order by e.eventDateTime desc
    """)
    List<EventRegistration> findAllByUserIdWithEvent(Long userId);
}
