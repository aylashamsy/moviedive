package com.deuceng.moviedive.dto;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dtomovie
{
    private String title;
    private String genre;
    private int releaseYear;
    private Double vote_average;
    private String director_name;
}
