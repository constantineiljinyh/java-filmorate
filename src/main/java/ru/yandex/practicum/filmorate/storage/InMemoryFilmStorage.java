package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements Storage<Film> {
    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private int idFilm = 1;

    public Film add(Film film) {
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пустой фильм");
        }
        film.setId(idFilm++);
        filmsMap.put(film.getId(), film);
        return film;
    }

    public List<Film> getAll() {
        return new ArrayList<>(filmsMap.values());
    }

    public Film getById(Integer filmId) {
        if (filmsMap.containsKey(filmId)) {
            return filmsMap.get(filmId);
        } else {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
    }

    public Film update(Film updatedFilm) {
        if (!filmsMap.containsKey(updatedFilm.getId())) {
            throw new NotFoundException("Такого фильма нет");
        }

        filmsMap.put(updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    public Film remove(Integer filmId) {
        Film film = filmsMap.remove(filmId);
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID " + filmId + " не найден");
        }
        return film;
    }
}
