package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.entity.RelationTypeEnum;
import com.deuceng.moviedive.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoUserMovieStatus
{
    private User user;
    private Movie movie;
    private boolean liked;
    private RelationTypeEnum relation_type;
}
