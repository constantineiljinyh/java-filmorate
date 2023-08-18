package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeFilmsRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmStorage;

    private final UserService userService;

    private final LikeFilmsRepository likeFilms;

    @Transactional
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

    @Transactional
    public Film updateFilm(Film updatedFilm) {
        if (!filmStorage.isExist(updatedFilm.getId())) {
            log.debug("Поступил запрос на обновление не существующего фильма с id {}", updatedFilm);
            throw new NotFoundException("Фильм не найден.");
        }
        validateFilm(updatedFilm);
        Film updated = filmStorage.update(updatedFilm);
        log.info("Фильм обновлен: {}", updated);
        return updated;
    }

    @Transactional
    public void likeFilm(Integer filmId, Integer userId) {
        if (!userService.isExist(userId)) {
            log.debug("Поступил запрос на добавление лайка от несуществующего пользователя с id {}.", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не зарегистрирован.", userId));
        }
        if (!filmStorage.isExist(filmId)) {
            log.debug("Поступил запрос на добавление лайка у несуществующего фильма с id {}.", filmId);
            throw new NotFoundException(String.format("Фильм с id %d не не существует.", filmId));
        }
        if (!likeFilms.isExistLike(filmId, userId)) {
            log.debug("Повторный лайк от пользователя с id {}.", userId);
            throw new NotFoundException(String.format("Лайк от пользователя %d уже есть.", userId));
        }
        likeFilms.likeForFilm(filmId, userId);
    }

    @Transactional
    public void unlikeFilm(Integer filmId, Integer userId) {
        if (!userService.isExist(userId)) {
            log.debug("Поступил запрос на удаление лайка от несуществующего пользователя с id {}", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не зарегистрирован.", userId));
        }
        if (!filmStorage.isExist(filmId)) {
            log.debug("Поступил запрос на удаление лайка у несуществующего фильма с id {}", filmId);
            throw new NotFoundException(String.format("Фильм с id %d не не существует.", filmId));
        }
        if (likeFilms.isExistLike(filmId, userId)) {
            log.debug("Лайка от пользователя с id {} нету.", userId);
            throw new NotFoundException(String.format("Лайка от пользователя %d еще нет.", userId));
        }
        likeFilms.dislikeForFilm(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAll();
        log.debug("Пользователь запросил топ {} фильмов", count);
        return allFilms.stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Transactional
    public Film remove(Integer filmId) {
        if (filmStorage.getById(filmId) == null) {
            log.debug("Фильм равен null id {}", filmId);
            throw new NotFoundException("Фильм равен null");
        }
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