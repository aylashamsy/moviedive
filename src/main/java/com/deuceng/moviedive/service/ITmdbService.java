package com.deuceng.moviedive.service;

public interface ITmdbService
{
    void importPopularMoviesOnce();
    void importAllMoviesWithDiscover();
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
