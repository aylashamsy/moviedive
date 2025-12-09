package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.entity.Genre;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.repository.GenreRepository;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.service.ITmdbService;
import com.deuceng.moviedive.dto.TmdbMovieDto;
import com.deuceng.moviedive.dto.TmdbMovieResponseDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TmdbImportServiceImpl implements ITmdbService {

    @Autowired
    GenreRepository genreRepository;

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    public TmdbImportServiceImpl(RestTemplate restTemplate, MovieRepository movieRepository) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
    }

    @Override
    public void importPopularMoviesOnce() {

        // 1) İstek atacağımız URL'i oluştur
        String url = baseUrl + "/movie/popular"
                + "?api_key=" + apiKey
                + "&language=en-US"
                + "&page=1";

        // 2) TMDB'ye isteği at ve cevabı TmdbMovieResponse'a çevir
        TmdbMovieResponseDto response = restTemplate.getForObject(url, TmdbMovieResponseDto.class);

        if (response == null || response.getResults() == null) {
            System.out.println("TMDB'den film gelmedi.");
            return;
        }

        // 3) Gelen her film için Movie entity'si oluştur ve kaydet
        for (int i = 0; i < response.getResults().size(); i++) {
            TmdbMovieDto dto = response.getResults().get(i);
            /*
            // Aynı tmdbId varsa tekrar ekleme (istersen)
            if (movieRepository.findById(dto.getTmdbId()).isPresent()) {
                continue;
            }
             */

            Movie movie = new Movie();
            BeanUtils.copyProperties(dto, movie, "GeneralId");

            if (dto.getPoster_path() != null) {
                movie.setPoster_path("https://image.tmdb.org/t/p/w500" + dto.getPoster_path());
            }

            /*
            List<Genre> genrelist=new ArrayList();
            Dtogenre dtogenre = new Dtogenre();


             */

            List<Genre> genres = dto.getGenre_ids().stream()
                    .map(genreId -> genreRepository.findById(genreId.longValue())
                            .orElseGet(() -> {
                                Genre g = new Genre();
                                g.setId(genreId.longValue());
                                g.setName(resolveGenreName(genreId));
                                return genreRepository.save(g);
                            }))
                    .collect(Collectors.toList());

            movie.setGenres(genres);



            movieRepository.save(movie);
        }


        System.out.println("TMDB'den film import işlemi bitti.");
    }

    private String resolveGenreName(Integer genreId) {
        return switch (genreId) {
            case 28    -> "Action";
            case 12    -> "Adventure";
            case 16    -> "Animation";
            case 35    -> "Comedy";
            case 80    -> "Crime";
            case 99    -> "Documentary";
            case 18    -> "Drama";
            case 10751 -> "Family";
            case 14    -> "Fantasy";
            case 36    -> "History";
            case 27    -> "Horror";
            case 10402 -> "Music";
            case 9648  -> "Mystery";
            case 10749 -> "Romance";
            case 878   -> "Science Fiction";
            case 10770 -> "TV Movie";
            case 53    -> "Thriller";
            case 10752 -> "War";
            case 37    -> "Western";
            default -> "unknown";
        };
    }
}
