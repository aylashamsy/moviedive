package com.deuceng.moviedive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="director")
public class Director
{
    @Id
    @Column(name="director_id", nullable = false)
    private Long id;

    @Column(name = "director_name", nullable = false, length = 150)
    private String name;
}
