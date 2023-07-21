package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class InMemoryFilmService implements FilmService {
    private final Storage<Film> filmStorage;
    private final UserService userService;

    public InMemoryFilmService(Storage<Film> filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        try {
            validateFilm(film);
            Film addedFilm = filmStorage.add(film);
            log.info("Фильм добавлен: {}", addedFilm);
            return addedFilm;
        } catch (ValidationException e) {
            log.error("Ошибка добавления фильма: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.getAll();
    }

    public Film getFilmById(Integer filmId) {
        log.info("Получение пользователя по ID: {}", filmId);
        return filmStorage.getById(filmId);
    }

    public Film updateFilm(Film updatedFilm) {
        try {
            validateFilm(updatedFilm);
            Film updated = filmStorage.update(updatedFilm);
            log.info("Фильм обновлен: {}", updated);
            return updated;
        } catch (ValidationException e) {
            log.error("Ошибка обновления фильма: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public void likeFilm(Integer filmId, Integer userId) {
        User user = userService.getUserById(userId);
        Film film = filmStorage.getById(filmId);
        if (user != null && film != null) {
            film.setLikesCount(film.getLikesCount() + 1);
            log.info("Добавление лайка к фильму с ID {} от пользователя с ID {}", filmId, userId);
            filmStorage.update(film);
        } else {
            throw new NotFoundException("Пользователь или фильм с не найден");
        }
    }

    public void unlikeFilm(Integer filmId, Integer userId) {
        User user = userService.getUserById(userId);
        Film film = filmStorage.getById(filmId);
        if (user != null && film != null) {
            int likesCount = film.getLikesCount();
            if (likesCount > 0) {
                film.setLikesCount(likesCount - 1);
                log.info("Удаление лайка у фильма с ID {} от пользователя с ID {}", filmId, userId);
                filmStorage.update(film);
            }
        } else {
            throw new NotFoundException("Пользователь или фильм с не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAll();
        allFilms.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        log.info("Получение популярных фильмов ({} шт.)", count);
        return allFilms.subList(0, Math.min(count, allFilms.size()));
    }

    public Film remove(Integer filmId) {
        log.info("Удаление фильма с ID {} ", filmId);
        return filmStorage.remove(filmId);
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