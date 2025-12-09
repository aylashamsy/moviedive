package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.Genre;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TmdbMovieDto {

    private Long id;             // TMDB id
    private String title;
    private String vote_count;
    private String overview;
    private String release_date; // JSON'da bu isimle geliyor
    private Double vote_average;
    private String poster_path;
    private List<Integer> genre_ids=new ArrayList<>();//Dış API’ler gereksiz veri taşımamak için sadece id dönerler, bunun için genre integer kullan
}
