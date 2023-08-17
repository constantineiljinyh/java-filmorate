package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorageDbImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreFilmStorageDbImplTest {
    @Autowired
    private FilmStorageDbImpl filmDbStorage;

    @Autowired
    private GenreFilmStorageDbImpl genreDbStorage;

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAll();
        assertEquals(6, genres.size());
    }

    @Test
    public void testGetGenreById() {
        int genreId = 1;
        Genre genre = genreDbStorage.getById(genreId);
        assertNotNull(genre);
        assertEquals(genreId, genre.getId());
    }

    @Test
    public void testGetGenresForFilm() {
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

        Set<Genre> expectedGenres = new LinkedHashSet<>();
        expectedGenres.add(genre);

        Set<Genre> actualGenres = genreDbStorage.getForFilm(addedFilm.getId());

        assertEquals(expectedGenres, actualGenres);
    }

    @Test
    public void testIsGenreExist() {
        int existingGenreId = 1;
        int nonExistentGenreId = 100;

        assertTrue(genreDbStorage.isExist(existingGenreId));
        assertFalse(genreDbStorage.isExist(nonExistentGenreId));
    }
}
