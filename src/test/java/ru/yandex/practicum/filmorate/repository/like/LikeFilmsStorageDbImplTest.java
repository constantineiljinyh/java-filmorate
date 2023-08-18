package ru.yandex.practicum.filmorate.repository.like;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LikeFilmsStorageDbImplTest {
    @Autowired
    private LikeFilmsRepository likeFilmsStorageDb;

    @Autowired
    private FilmRepository filmDbStorage;

    @Autowired
    private UserRepository userDbStorage;

    @BeforeEach
    public void setUp() {
        Film filmToAdd = new Film();
        filmToAdd.setName("Film Name");
        filmToAdd.setDescription("Film Description");
        filmToAdd.setReleaseDate(LocalDate.of(2022, 1, 1));
        filmToAdd.setDuration(120);
        filmToAdd.setMpa(new RatingMPA(1, "G"));
        filmToAdd.setRate(5);

        filmDbStorage.add(filmToAdd);

        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        userDbStorage.add(userToAdd);
    }

    @Test
    public void testAddLike() {
        int filmId = 1;
        int userId = 1;

        likeFilmsStorageDb.addLike(filmId, userId);

        int likeCount = likeFilmsStorageDb.getLikeCount(filmId);
        assertEquals(1, likeCount);
    }

    @Test
    public void testRemoveLike() {
        int filmId = 1;
        int userId = 1;

        likeFilmsStorageDb.addLike(filmId, userId);
        likeFilmsStorageDb.removeLike(filmId, userId);

        int likeCount = likeFilmsStorageDb.getLikeCount(filmId);
        assertEquals(0, likeCount);
    }

    @Test
    public void testLikeForFilm() {
        int filmId = 1;
        int userId = 1;

        likeFilmsStorageDb.likeForFilm(filmId, userId);

        int likeCount = likeFilmsStorageDb.getLikeCount(filmId);
        assertEquals(1, likeCount);
    }

    @Test
    public void testDislikeForFilm() {
        int filmId = 1;
        int userId = 1;

        likeFilmsStorageDb.likeForFilm(filmId, userId);
        likeFilmsStorageDb.dislikeForFilm(filmId, userId);

        int likeCount = likeFilmsStorageDb.getLikeCount(filmId);
        assertEquals(0, likeCount);
    }
}