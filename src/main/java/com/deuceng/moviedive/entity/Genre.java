package com.deuceng.moviedive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="genre")
public class Genre
{
    @Id
    private Long id;          // TMDB genre id’yi direkt kullan
    @Column
    private String name;
}
