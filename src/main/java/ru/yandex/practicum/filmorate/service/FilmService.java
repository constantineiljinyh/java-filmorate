package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    List<Film> getAllFilms();

    Film updateFilm(Film updatedFilm);

    void likeFilm(Integer filmId, Integer userId);

    void unlikeFilm(Integer filmId, Integer userId);

    List<Film> getPopularFilms(int count);

    Film getFilmById(Integer filmId);
}
