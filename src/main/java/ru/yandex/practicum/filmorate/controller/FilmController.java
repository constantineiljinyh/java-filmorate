package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private int idFilm = 1;

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(idFilm++);
            filmsMap.put(film.getId(), film);
            log.info("Фильм добавлен: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка добавления фильма: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NullPointerException e) {
            log.error("Ошибка добавления фильма: Передан пустой фильм");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка добавления фильма: Передан пустой фильм");
        }
    }

    @GetMapping()
    public List<Film> getALLFilms() {
        return new ArrayList<>(filmsMap.values());
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        try {
            validateFilm(updatedFilm);
            if (filmsMap.containsKey(updatedFilm.getId())) {
                filmsMap.put(updatedFilm.getId(), updatedFilm);
                log.info("Фильм обновлен: {}", updatedFilm);
                return updatedFilm;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого фильма нет");
            }
        } catch (ValidationException e) {
            log.error("Ошибка обновления фильма: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть до 28.12.1895");
        }
    }
}
