package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    List<Film> getAllFilms();

    Film updateFilm(Film updatedFilm);

    Film getFilmById(Integer filmId);
}
