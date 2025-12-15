package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="movie")
public class Movie
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tmdb_id", unique = true, nullable = false)
    private Long tmdbId;

    @Column(name="release_year")
    private String release_date;

    //@Column
    //private List<String> cast;

    @Column(name="rating")
    private Double vote_average;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @ManyToMany
    @JoinTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres=new ArrayList<>();

    /*
    @Column(name="director_name")
    private String directorName;
     */

    @Column
    private String vote_count;

    @Column
    private String title;

    @Column(name="postre_url")
    private String poster_path;

    @ManyToMany
    private List<Director> directors = new ArrayList<>();

    @ManyToMany
    private List<CastMember> castMembers = new ArrayList<>();
}
