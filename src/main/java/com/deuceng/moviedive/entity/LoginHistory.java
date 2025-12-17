package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "login_history")
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="session_id", nullable = false, length = 128)
    private String sessionId;

    @Column(name="login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name="logout_at")
    private LocalDateTime logoutAt;
}
