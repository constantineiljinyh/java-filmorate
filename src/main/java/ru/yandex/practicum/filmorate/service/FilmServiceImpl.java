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
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.like.LikeFilmsStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final Storage<Film> filmStorage;

    private final UserService userService;

    private final LikeFilmsStorage likeFilms;

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
        if (!filmStorage.isExist(updatedFilm.getId())) {
            throw new NotFoundException("Фильм не найден.");
        }
        validateFilm(updatedFilm);
        Film updated = filmStorage.update(updatedFilm);
        log.info("Фильм обновлен: {}", updated);
        return updated;

    }

    public void likeFilm(Integer filmId, Integer userId) {
        if (!userService.isExist(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не зарегистрирован.", userId));
        }
        if (!filmStorage.isExist(filmId)) {
            throw new NotFoundException(String.format("Фильм с id %d не не существует.", filmId));
        }
        if (!likeFilms.isExistLike(filmId, userId)) {
            throw new NotFoundException(String.format("Лайк от пользователя %d уже есть.", userId));
        }
        likeFilms.likeForFilm(filmId, userId);
    }

    public void unlikeFilm(Integer filmId, Integer userId) {
        if (!userService.isExist(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не зарегистрирован.", userId));
        }
        if (!filmStorage.isExist(filmId)) {
            throw new NotFoundException(String.format("Фильм с id %d не не существует.", filmId));
        }
        if (likeFilms.isExistLike(filmId, userId)) {
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