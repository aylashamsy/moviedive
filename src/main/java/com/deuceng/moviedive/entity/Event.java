package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_name", nullable = false, length = 120)
    private String eventName;

    @Column(nullable = false, length = 80)
    private String country;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime  eventDate;

    @Column(name = "creation_time", nullable = false, updatable = false)
    private LocalDateTime creationTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY) //lazy demek event tablosundan veri cekildiginde user tablosu
                                    // tetiklenmiyor gerektigi zaman cagriliyor(tablolar joinlanir)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<EventRegistration> registrations = new java.util.ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (this.creationTime == null) {
            this.creationTime = LocalDateTime.now();
        }
    }

}


