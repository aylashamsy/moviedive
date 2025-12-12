package com.deuceng.moviedive.starter;

import com.deuceng.moviedive.service.impl.TmdbImportServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = {"com.deuceng.moviedive.entity"})//Entitylerimizi görmesini sağladık
@ComponentScan(basePackages = {"com.deuceng.moviedive"})//Classlarımızın anatosyanlarını bul
@EnableJpaRepositories(basePackages = {"com.deuceng.moviedive.repository"})//Jpa repository'in aktif olması için
public class MoviediveApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviediveApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(TmdbImportServiceImpl tmdbImportService)
    {
        return args -> {
            tmdbImportService.importPopularMoviesOnce();
        };
    }
}

