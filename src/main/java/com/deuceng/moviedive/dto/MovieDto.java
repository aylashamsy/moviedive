package com.deuceng.moviedive.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MovieDto {
    @JsonProperty("id")
    private Long Id;
    private String title;
    private String vote_count;
    private String overview;
    private String release_date; // JSON'da bu isimle geliyor
    private Double vote_average;
    private String poster_url;
    private List<String> genre_names=new ArrayList<>();
    private List<String> director_names=new ArrayList<>();
    private List<String> actors=new ArrayList<>();
}
