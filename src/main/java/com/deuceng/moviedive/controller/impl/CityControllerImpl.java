package com.deuceng.moviedive.controller.impl;

import com.deuceng.moviedive.entity.City;
import com.deuceng.moviedive.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/api/movie_dive/cities")
@RequiredArgsConstructor
public class CityControllerImpl {

    private final CityRepository cityRepository;

    @GetMapping
    public List<City> getAllCities() {
        return cityRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();
    }
}
