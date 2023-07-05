package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void setUP() {
        filmController = new FilmController();
    }

    @Test
    void addFilm_ValidFilm() {
        Film validFilm = new Film();
        validFilm.setName("Film Name");
        validFilm.setDescription("Film Description");
        validFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
        validFilm.setDuration(120);

        Film addedFilm = filmController.addFilm(validFilm);

        Assertions.assertEquals(validFilm, addedFilm);
        Assertions.assertEquals(1, addedFilm.getId());
    }

    @Test
    void addFilm_EmptyName() {
        Film filmWithEmptyName = new Film();
        filmWithEmptyName.setName("");
        filmWithEmptyName.setDescription("Film Description");
        filmWithEmptyName.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmWithEmptyName.setDuration(120);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.addFilm(filmWithEmptyName));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Название не может быть пустым", exception.getReason());
    }

    @Test
    void addFilm_InvalidReleaseDate() {
        Film filmWithInvalidReleaseDate = new Film();
        filmWithInvalidReleaseDate.setName("Film Name");
        filmWithInvalidReleaseDate.setDescription("Film Description");
        filmWithInvalidReleaseDate.setReleaseDate(LocalDate.of(1890, 1, 1));
        filmWithInvalidReleaseDate.setDuration(120);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.addFilm(filmWithInvalidReleaseDate));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Неправильная дата релиза", exception.getReason());
    }

    @Test
    void addFilm_NegativeDuration() {
        Film filmWithNegativeDuration = new Film();
        filmWithNegativeDuration.setName("Film Name");
        filmWithNegativeDuration.setDescription("Film Description");
        filmWithNegativeDuration.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmWithNegativeDuration.setDuration(-120);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.addFilm(filmWithNegativeDuration));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Продолжительность фильма должна быть положительной", exception.getReason());
    }

    @Test
    void addFilm_LongDescription() {
        String longDescription = "A".repeat(201);

        Film filmWithLongDescription = new Film();
        filmWithLongDescription.setName("Film Name");
        filmWithLongDescription.setDescription(longDescription);
        filmWithLongDescription.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmWithLongDescription.setDuration(120);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.addFilm(filmWithLongDescription));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Максимальная длина описания — 200 символов", exception.getReason());
    }

    @Test
    void addFilm_NullFilm() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.addFilm(null));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Ошибка добавления фильма: Передан пустой фильм", exception.getReason());
    }

    @Test
    void updateFilm_ValidFilm() {
        Film existingFilm = new Film();
        existingFilm.setId(1);
        existingFilm.setName("Film Name");
        existingFilm.setDescription("Film Description");
        existingFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
        existingFilm.setDuration(120);

        filmController.addFilm(existingFilm);

        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Updated Film Name");
        updatedFilm.setDescription("Updated Film Description");
        updatedFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        updatedFilm.setDuration(150);

        Film result = filmController.updateFilm(updatedFilm);

        Assertions.assertEquals(updatedFilm, result);
        Assertions.assertEquals("Updated Film Name", result.getName());
        Assertions.assertEquals("Updated Film Description", result.getDescription());
        Assertions.assertEquals(LocalDate.of(2023, 1, 1), result.getReleaseDate());
        Assertions.assertEquals(150, result.getDuration());
    }

    @Test
    void updateFilm_NonExistingFilm() {
        Film nonExistingFilm = new Film();
        nonExistingFilm.setId(100);
        nonExistingFilm.setName("Film");
        nonExistingFilm.setDescription("Film Description");
        nonExistingFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
        nonExistingFilm.setDuration(120);

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                filmController.updateFilm(nonExistingFilm));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Assertions.assertEquals("Такого фильма нет", exception.getReason());
    }

    @Test
    void getALLFilms_ReturnsListOfFilms() {
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Фильм 1");
        film1.setDescription("Описание фильма 1");
        film1.setReleaseDate(LocalDate.of(2022, 1, 1));
        film1.setDuration(120);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Фильм 2");
        film2.setDescription("Описание фильма 2");
        film2.setReleaseDate(LocalDate.of(2023, 1, 1));
        film2.setDuration(150);
        filmController.addFilm(film2);

        List<Film> expectedFilms = Arrays.asList(film1, film2);

        List<Film> actualFilms = filmController.getALLFilms();

        Assertions.assertEquals(expectedFilms, actualFilms);
    }
    @Test
    void getALLFilms_ReturnsEmptyListOfFilms() {
        List<Film> films = filmController.getALLFilms();

        Assertions.assertTrue(films.isEmpty());
    }
}