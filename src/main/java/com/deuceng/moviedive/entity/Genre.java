package com.deuceng.moviedive.entity;

import jakarta.persistence.*;
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
    private Integer id;          // TMDB genre id’yi direkt kullan
    @Transient//Bu alan entity’de var, ama veritabanında yok
    public String getName()
    {
        return GenreEnum.fromId(id).getName();
    }
}
