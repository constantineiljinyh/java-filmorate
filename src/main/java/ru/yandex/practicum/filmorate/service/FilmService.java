package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {

    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private int idFilm = 1;

    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(idFilm++);
        filmsMap.put(film.getId(), film);
        return film;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmsMap.values());
    }

    public Film updateFilm(Film updatedFilm) {
        validateFilm(updatedFilm);
        if (filmsMap.containsKey(updatedFilm.getId())) {
            filmsMap.put(updatedFilm.getId(), updatedFilm);
            return updatedFilm;
        } else {
            throw new ValidationException("Такого фильма нет");
        }
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Ошибка добавления фильма: Передан пустой фильм");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть до 28.12.1895");
        }
    }
}