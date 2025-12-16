package com.deuceng.moviedive.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
public class TmdbCastResponseDto {
    private List<DtoCast> cast=new ArrayList<>();
    private List<DtoCrew> crew=new ArrayList<>();
}
