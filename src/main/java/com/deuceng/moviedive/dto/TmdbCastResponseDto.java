package com.deuceng.moviedive.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
public class TmdbCastResponseDto {
    private Set<DtoCast> cast=new HashSet<>();;
    private Set<DtoCrew> crew=new HashSet<>();;
}
