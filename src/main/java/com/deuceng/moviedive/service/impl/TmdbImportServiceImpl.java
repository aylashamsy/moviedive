package com.deuceng.moviedive.service.impl;

// git commit -m "Authentication, Exception, Cast ve Crew Dto eklendi ve ona göre uygun istek atıldı TMDB'e. User service ve controller'ı baştan oluşturuldu."
import com.deuceng.moviedive.dto.*;
import com.deuceng.moviedive.entity.CastMember;
import com.deuceng.moviedive.entity.Director;
import com.deuceng.moviedive.entity.Genre;
import com.deuceng.moviedive.entity.Movie;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.repository.CastMemberRepository;
import com.deuceng.moviedive.repository.DirectorRepository;
import com.deuceng.moviedive.repository.GenreRepository;
import com.deuceng.moviedive.repository.MovieRepository;
import com.deuceng.moviedive.service.ITmdbService;
import jakarta.validation.constraints.Max;
import org.apache.logging.log4j.util.Cast;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.deuceng.moviedive.exception.MessageType.NO_RECORD_EXIST;

@Service
public class TmdbImportServiceImpl implements ITmdbService {

    @Autowired
    GenreRepository genreRepository;
    @Autowired
    DirectorRepository dirtorRepository;
    @Autowired
    CastMemberRepository castMemberRepository;

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;
    @Autowired
    private DirectorRepository directorRepository;


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

            if (dto.getTmdbId() == null) {
                continue;
            }

            Movie movie = new Movie();
            BeanUtils.copyProperties(dto, movie, "id", "tmdbId");

            movie.setTmdbId(dto.getTmdbId());


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


        System.out.println("TMDB'den popular film import işlemi bitti.");
    }

    @Override
    public void importAllMoviesWithDiscover() {

        int page = 1;
        final int totalPages = 50;

        while (page <= totalPages) {

            // 1) Discover URL
            String movie_url = baseUrl + "/discover/movie"
                    + "?api_key=" + apiKey
                    + "&language=en-US"
                    + "&include_adult=false"
                    + "&include_video=false"
                    + "&sort_by=popularity.desc"
                    + "&vote_count.gte=200"
                    + "&page=" + page;

            // 2) Gelen JSON cevabı alır ve onu TmdbMovieResponseDto adlı Java nesnesine otomatik çevirir.
            TmdbMovieResponseDto movie_response = restTemplate.getForObject(movie_url, TmdbMovieResponseDto.class);


            if (movie_response == null || movie_response.getResults() == null) {
                throw new BaseException(new ErrorMessage(NO_RECORD_EXIST,"TMDB'den film gelmedi." ,page));
            }


            // 3) Save movies
            for (int i=0;i<movie_response.getResults().size();i++) {
                TmdbMovieDto dto = movie_response.getResults().get(i);

                Long movie_id = dto.getTmdbId();
                String cast_url = baseUrl + "/movie/"+movie_id+"/credits"+ "?api_key=" + apiKey+ "&language=en-US";;

                TmdbCastResponseDto human_response= restTemplate.getForObject(cast_url, TmdbCastResponseDto.class);

                if (human_response == null || human_response.getCast() == null|human_response.getCrew()==null) {
                    throw new BaseException(new ErrorMessage(NO_RECORD_EXIST,"TMDB'den cast ve crew gelmedi." ,null));
                }


                    //Duplicate kontrolü TMDB movie id üzerinden olmalı
                     //dto.getId() TMDB'nin movie id'si olmalı
                if (dto.getTmdbId() == null) {
                    continue;
                }

                if (movieRepository.existsByTmdbId(dto.getTmdbId())) {
                    continue;
                }


                Movie movie = new Movie();

                BeanUtils.copyProperties(dto, movie, "id", "tmdbId");
                movie.setTmdbId(dto.getTmdbId());

                // poster
                if (dto.getPoster_path() != null) {
                    movie.setPoster_path("https://image.tmdb.org/t/p/w500" + dto.getPoster_path());
                }

                 // Directors = crew içinde job=Director
            List<Director> directors = human_response.getCrew().stream()
            .filter(c -> "Director".equalsIgnoreCase(c.getJob()))
            .map(c -> directorRepository.findById(c.getId())
                    .orElseGet(() -> {
                        Director d = new Director();
                        d.setId(c.getId());
                        d.setName(c.getName());
                        return directorRepository.save(d);
                    }))
            .toList();

    // Cast members = cast listesi
    List<CastMember> castMembers = human_response.getCast().stream()
            .map(c -> castMemberRepository.findById(c.getId())
                    .orElseGet(() -> {
                        CastMember cm = new CastMember();
                        cm.setId(c.getId());
                        cm.setName(c.getName());
                        return castMemberRepository.save(cm);
                    }))
            .toList();

                // genres (senin mevcut kod)
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
                movie.setCastMembers(castMembers);
                movie.setDirectors(directors);

                movieRepository.save(movie);
            }

            System.out.println("Discover imported page=" + page + "/" + totalPages
                    + " | count=" + movie_response.getResults().size());

            page++;

        }

        System.out.println("Tmdb'den discover import bitti. lastPage=" + (page - 1)
                + " totalPages=" + totalPages);
    }


    private String resolveGenreName(Integer genreId) {
        return switch (genreId) {
            case 28 -> "Action";
            case 12 -> "Adventure";
            case 16 -> "Animation";
            case 35 -> "Comedy";
            case 80 -> "Crime";
            case 99 -> "Documentary";
            case 18 -> "Drama";
            case 10751 -> "Family";
            case 14 -> "Fantasy";
            case 36 -> "History";
            case 27 -> "Horror";
            case 10402 -> "Music";
            case 9648 -> "Mystery";
            case 10749 -> "Romance";
            case 878 -> "Science Fiction";
            case 10770 -> "TV Movie";
            case 53 -> "Thriller";
            case 10752 -> "War";
            case 37 -> "Western";
            default -> "unknown";
        };
    }

}


