package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Поступил запрос на добавление фильма: {}", film.getName());
        return filmService.addFilm(film);
    }

    @GetMapping()
    public List<Film> getAllFilms() {
        log.debug("Поступил запрос на просмотр всех фильмов.");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable("id") Integer filmId) {
        log.debug("Поступил запрос на просмотр фильма с id {}.", filmId);
        return filmService.getFilmById(filmId);
    }

    @DeleteMapping("/{id}")
    public Film removeFilm(@PathVariable("id") Integer filmId) {
        log.debug("Поступил запрос на удаление фильма с id {}.", filmId);
        return filmService.remove(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        log.debug("Поступил запрос на лайк фильма с id {} от пользователя с id {}.", filmId, userId);
        filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        log.debug("Поступил запрос на удаление лайка у фильма с id {} от пользователя с id {}.", filmId, userId);
        filmService.unlikeFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int limit) {
        log.debug("Поступил запрос на просмотр популярных фильмов (ограничение: {}).", limit);
        return filmService.getPopularFilms(limit);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.debug("Поступил запрос на обновление информации о фильме с id {}.", updatedFilm.getId());
        return filmService.updateFilm(updatedFilm);
    }
}
