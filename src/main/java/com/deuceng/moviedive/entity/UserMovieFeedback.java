package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;



@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_movie_feedback")
@Data
public class UserMovieFeedback {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = true)
    @DecimalMin(value = "1.0", inclusive = true)
    @DecimalMax(value = "10.0", inclusive = true)
    private Double score;  // 0-10 arası

    @ManyToOne
    @JoinColumn(name="comment_id", nullable = true)
    private Comment comment;
}