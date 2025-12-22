package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_movie_status")
public class UserMovieStatus
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int userMovieStatusId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column
    private boolean liked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RelationTypeEnum relation_type;
}
