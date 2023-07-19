package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public InMemoryFilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film updateFilm(Film updatedFilm) {
        validateFilm(updatedFilm);
        return filmStorage.updateFilm(updatedFilm);
    }

    public void likeFilm(Integer filmId, Integer userId) {
        User user = userService.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (user != null && film != null) {
            film.setLikesCount(film.getLikesCount() + 1);
            filmStorage.updateFilm(film);
        } else {
            throw new NotFoundException("Пользователь или фильм с не найден");
        }
    }

    public void unlikeFilm(Integer filmId, Integer userId) {
        User user = userService.getUserById(userId);
        Film film = filmStorage.getFilmById(filmId);
        if (user != null && film != null) {
            int likesCount = film.getLikesCount();
            if (likesCount > 0) {
                film.setLikesCount(likesCount - 1);
                filmStorage.updateFilm(film);
            }
        } else {
            throw new NotFoundException("Пользователь или фильм с не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> allFilms = filmStorage.getAllFilms();
        allFilms.sort(Comparator.comparingInt(Film::getLikesCount).reversed());
        return allFilms.subList(0, Math.min(count, allFilms.size()));
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