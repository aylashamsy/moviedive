package com.deuceng.moviedive.dto;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoCrew
{
    private Long id;
    private String name;
    private String job;
}
