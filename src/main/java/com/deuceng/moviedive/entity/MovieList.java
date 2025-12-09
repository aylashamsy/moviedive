package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movie_list")
public class MovieList {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinTable(
            name = "user_list",
            joinColumns = @JoinColumn(name = "movie_list_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User owner;

    @Column
    private String description;

    @Column
    private boolean isPublic;

    @ManyToMany
    @JoinTable(
            name = "movie_list_movies",
            joinColumns = @JoinColumn(name = "movie_list_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> movies = new HashSet<>();

    public <E> MovieList(Object o, String watchlist, String aDefault, User user, HashSet<E> es) {
    }
}
