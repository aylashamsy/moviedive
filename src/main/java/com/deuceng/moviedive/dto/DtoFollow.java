package com.deuceng.moviedive.dto;

import com.deuceng.moviedive.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoFollow
{
    private User follower;
    private User following;
}
