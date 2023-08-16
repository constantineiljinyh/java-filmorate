package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private Storage<Film> filmStorage;

    @Autowired
    private FilmController filmController;

    @Test
    void addFilmValidFilm() {
        Film validFilm = new Film();
        validFilm.setName("Film Name");
        validFilm.setDescription("Film Description");
        validFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
        validFilm.setDuration(120);
        validFilm.setMpa(new RatingMPA(1, "G"));
        validFilm.setRate(5);

        Film addedFilm = filmController.addFilm(validFilm);

        Assertions.assertEquals(validFilm, addedFilm);
        Assertions.assertEquals(1, addedFilm.getId());
    }

    @Test
    void addFilmInvalidReleaseDate() {
        Film filmWithInvalidReleaseDate = new Film();
        filmWithInvalidReleaseDate.setName("Film Name");
        filmWithInvalidReleaseDate.setDescription("Film Description");
        filmWithInvalidReleaseDate.setReleaseDate(LocalDate.of(1890, 1, 1));
        filmWithInvalidReleaseDate.setDuration(120);
        filmWithInvalidReleaseDate.setMpa(new RatingMPA(1, "G"));
        filmWithInvalidReleaseDate.setRate(5);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.addFilm(filmWithInvalidReleaseDate));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Дата релиза не может быть до 28.12.1895", exception.getReason());
    }

    @Test
    void addFilmNullFilm() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.addFilm(null));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Ошибка добавления фильма: Передан пустой фильм", exception.getReason());
    }

    @Test
    void updateFilmValidFilm() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Original Name");
        filmToAdd.setDescription("Original Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        filmController.addFilm(filmToAdd);

        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Updated Name");
        updatedFilm.setDescription("Updated Description");
        updatedFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        updatedFilm.setDuration(130);
        updatedFilm.setMpa(new RatingMPA(2, "PG"));
        updatedFilm.setRate(4);

        Film result = filmController.updateFilm(updatedFilm);

        Assertions.assertEquals(updatedFilm, result);
        Assertions.assertEquals("Updated Name", result.getName());
        Assertions.assertEquals("Updated Description", result.getDescription());
        Assertions.assertEquals(LocalDate.of(2023, 1, 1), result.getReleaseDate());
        Assertions.assertEquals(130, result.getDuration());
    }

    @Test
    void removeFilmValidFilmId() {
        Film filmToRemove = new Film();
        filmToRemove.setName("Film to Remove");
        filmToRemove.setDescription("Film to Remove Description");
        filmToRemove.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToRemove.setLikesCount(0);
        filmToRemove.setDuration(120);
        filmToRemove.setMpa(new RatingMPA(1, "G"));
        filmToRemove.setRate(5);

        Film addedFilm = filmController.addFilm(filmToRemove);
        Integer filmId = addedFilm.getId();

        Film removedFilm = filmController.removeFilm(filmId);

        Assertions.assertEquals(addedFilm, removedFilm);
    }

    @Test
    void removeFilmInvalidFilmId() {
        Integer nonExistentFilmId = 1000;

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                filmController.removeFilm(nonExistentFilmId));

        Assertions.assertEquals("Фильм с ID " + nonExistentFilmId + " не найден", exception.getMessage());
    }

    @Test
    void updateFilmNonExistingFilm() {
        Film nonExistingFilm = new Film();
        nonExistingFilm.setId(100);
        nonExistingFilm.setName("Film");
        nonExistingFilm.setDescription("Film Description");
        nonExistingFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
        nonExistingFilm.setDuration(120);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                filmController.updateFilm(nonExistingFilm));

        Assertions.assertEquals("Фильм не найден.", exception.getMessage());
    }

    @Test
    void getALLFilmsReturnsListOfFilms() {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2022, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new RatingMPA(1, "G"));
        film1.setRate(5);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        film2.setDuration(150);
        film2.setMpa(new RatingMPA(2, "PG"));
        film2.setRate(4);
        filmController.addFilm(film2);

        List<Film> expectedFilms = Arrays.asList(film1, film2);

        List<Film> actualFilms = filmController.getAllFilms();

        Assertions.assertEquals(expectedFilms, actualFilms);
    }

    @Test
    void getALLFilmsReturnsEmptyListOfFilms() {
        List<Film> films = filmController.getAllFilms();

        Assertions.assertTrue(films.isEmpty());
    }

    @Test
    void successfullyLikesFilm() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        filmController.addFilm(filmToAdd);

        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        userService.addUser(userToAdd);

        filmService.likeFilm(1, 1);

        Film likedFilm = filmStorage.getById(1);
        Assertions.assertEquals(1, likedFilm.getLikesCount());
    }

    @Test
    void likeFilmInvalidFilmId() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        filmController.addFilm(filmToAdd);

        Assertions.assertThrows(NotFoundException.class, () -> filmService.likeFilm(1, 1));
    }

    @Test
    void likeFilmInvalidUserId() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        filmController.addFilm(filmToAdd);

        Assertions.assertThrows(NotFoundException.class, () -> filmService.likeFilm(1, 1));
    }

    @Test
    void successFullyUnlikesFilm() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        filmController.addFilm(filmToAdd);

        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        userService.addUser(userToAdd);
        filmService.likeFilm(1, 1);

        filmService.unlikeFilm(1, 1);

        Film unlikedFilm = filmStorage.getById(1);
        Assertions.assertEquals(0, unlikedFilm.getLikesCount());
    }

    @Test
    void unlikeFilmInvalidFilmId() {
        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        userService.addUser(userToAdd);

        Assertions.assertThrows(NotFoundException.class, () -> filmService.unlikeFilm(1, 1));
    }

    @Test
    void unlikeFilmInvalidUserId() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        filmController.addFilm(filmToAdd);

        Assertions.assertThrows(NotFoundException.class, () -> filmService.unlikeFilm(1, 1));
    }

    @Test
    void getPopularFilmsPopularFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2022, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new RatingMPA(1, "G"));
        film1.setRate(5);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2022, 2, 1));
        film2.setDuration(130);
        film2.setMpa(new RatingMPA(2, "PG"));
        film2.setRate(4);
        filmController.addFilm(film2);

        Film film3 = new Film();
        film3.setName("Film 3");
        film3.setDescription("Description 4");
        film3.setReleaseDate(LocalDate.of(2020, 3, 1));
        film3.setDuration(140);
        film3.setMpa(new RatingMPA(2, "PG"));
        film3.setRate(7);
        filmController.addFilm(film3);

        List<Film> popularFilms = filmService.getPopularFilms(2);

        Assertions.assertEquals(2, popularFilms.size());
        Assertions.assertEquals(film3, popularFilms.get(0));
        Assertions.assertEquals(film1, popularFilms.get(1));
    }
}