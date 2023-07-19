package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private int idFilm = 1;

    public Film addFilm(Film film) {
        film.setId(idFilm++);
        filmsMap.put(film.getId(), film);
        return film;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmsMap.values());
    }

    public Film getFilmById(Integer filmId) {
        if (filmsMap.containsKey(filmId)) {
            return filmsMap.get(filmId);
        } else {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
    }

    public Film updateFilm(Film updatedFilm) {
        if (!filmsMap.containsKey(updatedFilm.getId())) {
            throw new NotFoundException("Такого фильма нет");
        }

        filmsMap.put(updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }
}
