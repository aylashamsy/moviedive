package com.deuceng.moviedive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name="cast_member_id")
    private Long id;

    @Column(name="cast_member_name")
    private String name;
}
