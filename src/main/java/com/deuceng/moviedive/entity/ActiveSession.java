package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "active_sessions")
public class ActiveSession {

    @Id
    @Column(name="session_id", length = 128)
    private String sessionId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name="last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;
}
