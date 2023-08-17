package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmStorageDbImplTest {
    @Autowired
    private FilmStorageDbImpl filmDbStorage;

    @Test
    public void testAddFilm() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        Film addedFilm = filmDbStorage.add(filmToAdd);

        assertNotNull(addedFilm);
        assertNotNull(addedFilm.getId());
        assertEquals(filmToAdd.getName(), addedFilm.getName());
        assertEquals(filmToAdd.getDescription(), addedFilm.getDescription());
        assertEquals(filmToAdd.getReleaseDate(), addedFilm.getReleaseDate());
        assertEquals(filmToAdd.getDuration(), addedFilm.getDuration());
        assertEquals(filmToAdd.getMpa(), addedFilm.getMpa());
        assertEquals(filmToAdd.getRate(), addedFilm.getRate());
    }

    @Test
    public void testGetAll() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2022, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new RatingMPA(1, "G"));
        film1.setRate(5);
        filmDbStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2022, 2, 1));
        film2.setDuration(130);
        film2.setMpa(new RatingMPA(2, "PG"));
        film2.setRate(4);
        filmDbStorage.add(film2);

        List<Film> films = filmDbStorage.getAll();

        assertFalse(films.isEmpty());

        Film firstFilm = films.get(0);
        assertEquals(film1.getId(), firstFilm.getId());
        assertEquals(film1.getName(), firstFilm.getName());
        assertEquals(film1.getDescription(), firstFilm.getDescription());
        assertEquals(film1.getReleaseDate(), firstFilm.getReleaseDate());
        assertEquals(film1.getDuration(), firstFilm.getDuration());
        assertEquals(film1.getMpa(), firstFilm.getMpa());
        assertEquals(film1.getRate(), firstFilm.getRate());

        Film secondFilm = films.get(1);
        assertEquals(film2.getId(), secondFilm.getId());
        assertEquals(film2.getName(), secondFilm.getName());
        assertEquals(film2.getDescription(), secondFilm.getDescription());
        assertEquals(film2.getReleaseDate(), secondFilm.getReleaseDate());
        assertEquals(film2.getDuration(), secondFilm.getDuration());
        assertEquals(film2.getMpa(), secondFilm.getMpa());
        assertEquals(film2.getRate(), secondFilm.getRate());
    }

    @Test
    public void testUpdateFilm() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Original Name");
        filmToAdd.setDescription("Original Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);
        Film addedFilm = filmDbStorage.add(filmToAdd);

        addedFilm.setName("Updated Name");
        addedFilm.setDescription("Updated Description");
        addedFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        addedFilm.setDuration(130);
        addedFilm.setMpa(new RatingMPA(2, "PG"));
        addedFilm.setRate(4);

        Film updatedFilm = filmDbStorage.update(addedFilm);

        assertNotNull(updatedFilm);
        assertEquals(addedFilm.getId(), updatedFilm.getId());
        assertEquals(addedFilm.getName(), updatedFilm.getName());
        assertEquals(addedFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(addedFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(addedFilm.getDuration(), updatedFilm.getDuration());
        assertEquals(addedFilm.getMpa(), updatedFilm.getMpa());
        assertEquals(addedFilm.getRate(), updatedFilm.getRate());

        Film retrievedFilm = filmDbStorage.getById(updatedFilm.getId());
        assertNotNull(retrievedFilm);
        assertEquals(addedFilm.getId(), retrievedFilm.getId());
        assertEquals(addedFilm.getName(), retrievedFilm.getName());
        assertEquals(addedFilm.getDescription(), retrievedFilm.getDescription());
        assertEquals(addedFilm.getReleaseDate(), retrievedFilm.getReleaseDate());
        assertEquals(addedFilm.getDuration(), retrievedFilm.getDuration());
        assertEquals(addedFilm.getMpa(), retrievedFilm.getMpa());
        assertEquals(addedFilm.getRate(), retrievedFilm.getRate());
    }

    @Test
    public void testGetFilmById() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        Film filmToAdd = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(120)
                .mpa(new RatingMPA(1, "G"))
                .rate(5)
                .genres(new HashSet<>(Collections.singletonList(genre)))
                .build();

        Film addedFilm = filmDbStorage.add(filmToAdd);
        assertNotNull(addedFilm.getId());

        Film retrievedFilm = filmDbStorage.getById(addedFilm.getId());

        assertNotNull(retrievedFilm);
        assertEquals(addedFilm.getId(), retrievedFilm.getId());
        assertEquals(addedFilm.getName(), retrievedFilm.getName());
        assertEquals(addedFilm.getDescription(), retrievedFilm.getDescription());
        assertEquals(addedFilm.getReleaseDate(), retrievedFilm.getReleaseDate());
        assertEquals(addedFilm.getDuration(), retrievedFilm.getDuration());
        assertEquals(addedFilm.getMpa(), retrievedFilm.getMpa());
        assertEquals(addedFilm.getRate(), retrievedFilm.getRate());

        assertNotNull(retrievedFilm.getGenres());
        assertFalse(retrievedFilm.getGenres().isEmpty());
        assertTrue(retrievedFilm.getGenres().contains(genre));
    }

    @Test
    public void testRemoveFilm() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        Film addedFilm = filmDbStorage.add(filmToAdd);

        Film removedFilm = filmDbStorage.remove(addedFilm.getId());

        assertNotNull(removedFilm);
        assertEquals(addedFilm.getId(), removedFilm.getId());
        assertEquals(addedFilm.getName(), removedFilm.getName());
        assertEquals(addedFilm.getDescription(), removedFilm.getDescription());
        assertEquals(addedFilm.getReleaseDate(), removedFilm.getReleaseDate());
        assertEquals(addedFilm.getDuration(), removedFilm.getDuration());
        assertEquals(addedFilm.getMpa(), removedFilm.getMpa());
        assertEquals(addedFilm.getRate(), removedFilm.getRate());

        assertThrows(NotFoundException.class, () -> filmDbStorage.getById(addedFilm.getId()));
    }

    @Test
    public void testIsExistReturnsTrue() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        Film addedFilm = filmDbStorage.add(filmToAdd);
        int existingFilmId = 1;

        assertTrue(filmDbStorage.isExist(existingFilmId));
    }

    @Test
    public void testIsExistReturnsFalse() {
        int nonExistentFilmId = -1;

        assertFalse(filmDbStorage.isExist(nonExistentFilmId));
    }

    @Test
    public void testIsExistGenreFilmReturnsTrue() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        Film filmToAdd = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(120)
                .mpa(new RatingMPA(1, "G"))
                .rate(5)
                .genres(new HashSet<>(Collections.singletonList(genre)))
                .build();

        Film addedFilm = filmDbStorage.add(filmToAdd);
        int existingFilmId = 1;
        int existingGenreId = 1;

        assertTrue(filmDbStorage.isExistGenreFilm(existingFilmId, existingGenreId));
    }

    @Test
    public void testIsExistGenreFilmReturnsFalse() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        Film addedFilm = filmDbStorage.add(filmToAdd);
        int existingFilmId = 1;
        int nonExistentGenreId = -1;

        assertFalse(filmDbStorage.isExistGenreFilm(existingFilmId, nonExistentGenreId));
    }
}



