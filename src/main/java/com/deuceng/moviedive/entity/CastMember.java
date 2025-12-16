package com.deuceng.moviedive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="cast_member")
public class CastMember
{
    @Id
    @Column(name="cast_member_id", nullable = false)
    private Long id;

    @Column(name="cast_member_name",nullable = false, length = 150)
    private String name;

    @Column(name = "popularity")
    private Double popularity;

    @Transient//DB'e yazılmaz
    @JsonIgnore//Responseda gözükmesin
    private String profile_path;


}
