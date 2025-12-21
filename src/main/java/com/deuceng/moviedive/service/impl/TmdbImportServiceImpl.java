package com.deuceng.moviedive.service.impl;
import java.util.ArrayList;
import java.util.stream.Collectors;
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
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;

import static com.deuceng.moviedive.exception.MessageType.NO_RECORD_EXIST;

@Service
public class TmdbImportServiceImpl implements ITmdbService {


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

    @Autowired
    GenreRepository genreRepository;

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

            // Aynı tmdbId varsa tekrar ekleme (istersen)
            if (movieRepository.findById(dto.getTmdbId()).isPresent()) {
                continue;
            }


            if (dto.getTmdbId() == null) {
                continue;
            }

            Movie movie = new Movie();
            BeanUtils.copyProperties(dto, movie, "id", "tmdbId");

            movie.setTmdbId(dto.getTmdbId());


            if (dto.getPoster_path() != null) {
                movie.setPoster_path("https://image.tmdb.org/t/p/w500" + dto.getPoster_path());
            }


            List<Genre> genres = dto.getGenre_ids().stream()
                    .map(id -> genreRepository.findById(id)
                            .orElseGet(() -> genreRepository.save(new Genre(id))))
                    .toList();

            movie.setGenres(genres);

            movieRepository.save(movie);
        }


        System.out.println("TMDB'den popular film import işlemi bitti.");
    }

    @Override
    public void importAllMoviesWithDiscover() {

        int page = 1;
        final int totalPages = 20;

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
                throw new BaseException(new ErrorMessage(NO_RECORD_EXIST, "TMDB'den film gelmedi.", page));
            }


            // 3) Save movies
            for (int i = 0; i < movie_response.getResults().size(); i++) {
                TmdbMovieDto dto = movie_response.getResults().get(i);

                Long movie_id = dto.getTmdbId();
                String cast_url = baseUrl + "/movie/" + movie_id + "/credits" + "?api_key=" + apiKey + "&language=en-US";

                TmdbCastResponseDto human_response = restTemplate.getForObject(cast_url, TmdbCastResponseDto.class);

                if (human_response == null || human_response.getCast() == null | human_response.getCrew() == null) {
                    throw new BaseException(new ErrorMessage(NO_RECORD_EXIST, "TMDB'den cast ve crew gelmedi.", null));
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
                        .sorted(Comparator.comparing(DtoCast::getPopularity,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(5)
                        .map(c -> castMemberRepository.findById(c.getId())
                                .orElseGet(() -> {
                                    CastMember cm = new CastMember();
                                    cm.setId(c.getId());
                                    cm.setName(c.getName());
                                    cm.setPopularity(c.getPopularity());
                                    cm.setProfile_path(c.getProfile_path());
                                    return castMemberRepository.save(cm);
                                }))
                        .toList();


                List<Genre> genres = dto.getGenre_ids().stream()
                        .map(id -> genreRepository.findById(id)
                                .orElseGet(() -> genreRepository.save(new Genre(id))))
                        .toList();

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

    @Override
    public void importTrendingMoviesOfWeekTop20() {

        String url = baseUrl + "/trending/movie/week"
                + "?api_key=" + apiKey
                + "&language=en-US"
                + "&page=1";

        TmdbMovieResponseDto response = restTemplate.getForObject(url, TmdbMovieResponseDto.class);

        if (response == null || response.getResults() == null) {
            System.out.println("TMDB'den trending (week) film gelmedi.");
            return;
        }

        List<TmdbMovieDto> top20 = response.getResults().stream()
                .limit(20)
                .collect(Collectors.toCollection(ArrayList::new)); // mutable olsun

        for (TmdbMovieDto dto : top20) {

            if (dto == null || dto.getTmdbId() == null) continue;

            // ✅ upsert: varsa update, yoksa insert
            Movie movie = movieRepository.findByTmdbId(dto.getTmdbId())
                    .orElseGet(Movie::new);

            // basic fields
            movie.setTmdbId(dto.getTmdbId());
            movie.setTitle(dto.getTitle());
            movie.setOverview(dto.getOverview());
            movie.setRelease_date(dto.getRelease_date());
            movie.setVote_average(dto.getVote_average());
            movie.setVote_count(dto.getVote_count()); // dto String ise entity de String olmalı; değilse parse et

            // poster (DB'de full URL tuttuğun için aynı şekilde)
            if (dto.getPoster_path() != null && !dto.getPoster_path().isBlank()) {
                movie.setPoster_path("https://image.tmdb.org/t/p/w500" + dto.getPoster_path());
            }

            // ✅ genres (mutlaka set et)
            if (dto.getGenre_ids() != null && !dto.getGenre_ids().isEmpty()) {
                List<Genre> genres = dto.getGenre_ids().stream()
                        .map(id -> genreRepository.findById(id)
                                .orElseGet(() -> genreRepository.save(new Genre(id))))
                        .collect(Collectors.toCollection(ArrayList::new)); // mutable

                movie.setGenres(genres);
            } else {
                // istersen boşla; istersen mevcut genres'ı koru (şu an koruyoruz)
                // movie.setGenres(new ArrayList<>());
            }

            // ✅ credits (cast + director) - 20 film için 20 ekstra istek
            Long movieId = dto.getTmdbId();
            String castUrl = baseUrl + "/movie/" + movieId + "/credits"
                    + "?api_key=" + apiKey
                    + "&language=en-US";

            TmdbCastResponseDto humanResponse = restTemplate.getForObject(castUrl, TmdbCastResponseDto.class);

            if (humanResponse == null || humanResponse.getCast() == null || humanResponse.getCrew() == null) {
                // Burada throw etmek yerine continue daha mantıklı olabilir (1 film bozuk diye tüm import durmasın)
                // Ama senin projede istersen throw kalsın.
                throw new BaseException(new ErrorMessage(NO_RECORD_EXIST, "TMDB'den cast ve crew gelmedi.", movieId));
            }

            // Directors
            List<Director> directors = humanResponse.getCrew().stream()
                    .filter(c -> c != null && "Director".equalsIgnoreCase(c.getJob()))
                    .map(c -> directorRepository.findById(c.getId())
                            .orElseGet(() -> {
                                Director d = new Director();
                                d.setId(c.getId());
                                d.setName(c.getName());
                                return directorRepository.save(d);
                            }))
                    .collect(Collectors.toCollection(ArrayList::new)); // ✅ mutable

            // Cast members (top 5 popularity)
            List<CastMember> castMembers = humanResponse.getCast().stream()
                    .filter(c -> c != null)
                    .sorted(Comparator.comparing(DtoCast::getPopularity,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(5)
                    .map(c -> castMemberRepository.findById(c.getId())
                            .orElseGet(() -> {
                                CastMember cm = new CastMember();
                                cm.setId(c.getId());
                                cm.setName(c.getName());
                                cm.setPopularity(c.getPopularity());
                                cm.setProfile_path(c.getProfile_path());
                                return castMemberRepository.save(cm);
                            }))
                    .collect(Collectors.toCollection(ArrayList::new)); // ✅ mutable

            movie.setDirectors(directors);
            movie.setCastMembers(castMembers);

            movieRepository.save(movie);
        }

        System.out.println("TMDB trending (week) top20 import bitti.");
    }
    @Override
    @Transactional
    public List<Movie> trendingMoviesOfWeekTop20() {

        String url = baseUrl + "/trending/movie/week"
                + "?api_key=" + apiKey
                + "&language=en-US"
                + "&page=1";

        TmdbMovieResponseDto response =
                restTemplate.getForObject(url, TmdbMovieResponseDto.class);

        if (response == null || response.getResults() == null) {
            System.out.println("TMDB'den trending (week) film gelmedi.");
            return List.of(); // boş liste
        }

        List<TmdbMovieDto> top20 = response.getResults().stream()
                .limit(20)
                .collect(Collectors.toCollection(ArrayList::new));

        List<Movie> importedMovies = new ArrayList<>(); // ✅ döneceğimiz liste

        for (TmdbMovieDto dto : top20) {

            if (dto == null || dto.getTmdbId() == null) continue;

            Movie movie = movieRepository.findByTmdbId(dto.getTmdbId())
                    .orElseGet(Movie::new);

            // basic fields
            movie.setTmdbId(dto.getTmdbId());
            movie.setTitle(dto.getTitle());
            movie.setOverview(dto.getOverview());
            movie.setRelease_date(dto.getRelease_date());
            movie.setVote_average(dto.getVote_average());
            movie.setVote_count(dto.getVote_count());

            if (dto.getPoster_path() != null && !dto.getPoster_path().isBlank()) {
                movie.setPoster_path("https://image.tmdb.org/t/p/w500" + dto.getPoster_path());
            }

            // genres
            if (dto.getGenre_ids() != null && !dto.getGenre_ids().isEmpty()) {
                List<Genre> genres = dto.getGenre_ids().stream()
                        .map(id -> genreRepository.findById(id)
                                .orElseGet(() -> genreRepository.save(new Genre(id))))
                        .collect(Collectors.toCollection(ArrayList::new));

                movie.setGenres(genres);
            }

            // credits
            Long movieId = dto.getTmdbId();
            String castUrl = baseUrl + "/movie/" + movieId + "/credits"
                    + "?api_key=" + apiKey
                    + "&language=en-US";

            TmdbCastResponseDto humanResponse =
                    restTemplate.getForObject(castUrl, TmdbCastResponseDto.class);

            if (humanResponse == null
                    || humanResponse.getCast() == null
                    || humanResponse.getCrew() == null) {
                continue; // 1 film bozuk diye tüm import durmasın
            }

            // directors
            List<Director> directors = humanResponse.getCrew().stream()
                    .filter(c -> c != null && "Director".equalsIgnoreCase(c.getJob()))
                    .map(c -> directorRepository.findById(c.getId())
                            .orElseGet(() -> {
                                Director d = new Director();
                                d.setId(c.getId());
                                d.setName(c.getName());
                                return directorRepository.save(d);
                            }))
                    .collect(Collectors.toCollection(ArrayList::new));

            // cast (top 5 popularity)
            List<CastMember> castMembers = humanResponse.getCast().stream()
                    .filter(c -> c != null)
                    .sorted(Comparator.comparing(
                            DtoCast::getPopularity,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ))
                    .limit(5)
                    .map(c -> castMemberRepository.findById(c.getId())
                            .orElseGet(() -> {
                                CastMember cm = new CastMember();
                                cm.setId(c.getId());
                                cm.setName(c.getName());
                                cm.setPopularity(c.getPopularity());
                                cm.setProfile_path(c.getProfile_path());
                                return castMemberRepository.save(cm);
                            }))
                    .collect(Collectors.toCollection(ArrayList::new));

            movie.setDirectors(directors);
            movie.setCastMembers(castMembers);

            Movie savedMovie = movieRepository.save(movie);

            importedMovies.add(savedMovie); // ✅ listeye ekle
        }

        System.out.println("TMDB trending (week) top20 import bitti.");

        return importedMovies; // ✅ artık List<Movie> dönüyor
    }

}


