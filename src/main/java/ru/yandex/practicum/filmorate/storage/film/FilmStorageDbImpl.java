package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.genre.GenreFilmStorageDbImpl;
import ru.yandex.practicum.filmorate.storage.like.LikeFilmsStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingMPAStorageDbImpl;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.storage.ModelMapper.createInsertFilmStatement;
import static ru.yandex.practicum.filmorate.storage.ModelMapper.mapperGetFilms;

@Component
@Primary
public class FilmStorageDbImpl implements Storage<Film> {

    private final JdbcTemplate jdbcTemplate;
    private final GenreFilmStorageDbImpl genreDbStorage;
    private final RatingMPAStorageDbImpl ratingMpaDbStorage;

    private final LikeFilmsStorage likeFilms;

    public FilmStorageDbImpl(JdbcTemplate jdbcTemplate, GenreFilmStorageDbImpl genreDbStorage, RatingMPAStorageDbImpl ratingMpaDbStorage, LikeFilmsStorage likeFilms) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.ratingMpaDbStorage = ratingMpaDbStorage;
        this.likeFilms = likeFilms;
    }

    @Override
    public Film add(Film film) {
        if (film == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пустой фильм");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        Integer[] genreIds = film.getGenres() != null
                ? film.getGenres().stream()
                .map(Genre::getId)
                .toArray(Integer[]::new)
                : new Integer[0];

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = createInsertFilmStatement(connection, film, genreIds);
            return ps;
        }, keyHolder);

        int generatedId = keyHolder.getKey().intValue();
        film.setId(generatedId);

        if (genreIds.length > 0) {
            String insertGenreSql = "INSERT INTO genre_film (id_film, id_genre) VALUES (?, ?)";
            for (Integer genreId : genreIds) {
                jdbcTemplate.update(insertGenreSql, generatedId, genreId);
            }
        }

        List<Genre> filmGenres = genreDbStorage.getForFilm(generatedId);
        film.setGenres(filmGenres);

        return film;
    }

    public List<Film> getAll() {
        String selectSql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating, f.id_ratingMPA, " +
                "lf.id_film AS like_film_id, COUNT(lf.id_film) AS like_count, gf.id_genre, g.name_genre, rm.name_MPA " +
                "FROM films f " +
                "LEFT JOIN like_film lf ON f.id = lf.id_film " +
                "LEFT JOIN genre_film gf ON f.id = gf.id_film " +
                "LEFT JOIN genre g ON gf.id_genre = g.id_genre " +
                "LEFT JOIN ratingMPA rm ON f.id_ratingMPA = rm.id_ratingMPA " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating, f.id_ratingMPA, " +
                "lf.id_film, gf.id_genre, g.name_genre, rm.name_MPA " +
                "ORDER BY f.id, gf.id_genre";

        return jdbcTemplate.query(selectSql, mapperGetFilms()).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public Film update(Film updatedFilm) {
        String updateSql = "UPDATE films SET duration = ?, name = ?, description = ?, rating = ?, release_date = ?, id_ratingMPA = ? WHERE id = ?";

        try {
            jdbcTemplate.update(updateSql,
                    updatedFilm.getDuration(),
                    updatedFilm.getName(),
                    updatedFilm.getDescription(),
                    updatedFilm.getRate(),
                    Timestamp.valueOf(updatedFilm.getReleaseDate().atStartOfDay()),
                    updatedFilm.getMpa().getId(),
                    updatedFilm.getId());

            String deleteGenresSql = "DELETE FROM genre_film WHERE id_film = ?";
            jdbcTemplate.update(deleteGenresSql, updatedFilm.getId());

            if (updatedFilm.getGenres() != null && !updatedFilm.getGenres().isEmpty()) {
                String insertGenreSql = "INSERT INTO genre_film (id_film, id_genre) VALUES (?, ?)";
                for (Genre genre : updatedFilm.getGenres()) {
                    if (!isExistGenreFilm(updatedFilm.getId(), genre.getId())) {
                        jdbcTemplate.update(insertGenreSql, updatedFilm.getId(), genre.getId());
                    }
                }
            }

            List<Genre> filmGenres = genreDbStorage.getForFilm(updatedFilm.getId());
            updatedFilm.setGenres(filmGenres);

            return updatedFilm;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Такого фильма нет");
        }
    }

    @Override
    public Film getById(Integer filmId) {
        String selectSql = "SELECT f.id, f.duration, f.name, f.description, f.rating, f.release_date, f.id_ratingMPA FROM films f WHERE f.id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(selectSql, (rs, rowNum) ->
                    new Film(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
                            rs.getInt("duration"),
                            likeFilms.getLikeCount(rs.getInt("id")),
                            rs.getInt("rating"),
                            genreDbStorage.getForFilm(rs.getInt("id")),
                            ratingMpaDbStorage.getById(rs.getInt("id_ratingMPA"))
                    ), filmId);

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
    }

    @Override
    public Film remove(Integer filmId) {
        Film filmToRemove = getById(filmId);

        if (filmToRemove == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID " + filmId + " не найден");
        }

        String deleteFilmSql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(deleteFilmSql, filmId);

        return filmToRemove;
    }

    @Override
    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT id " +
                "FROM films " +
                "WHERE id = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }

    public boolean isExistGenreFilm(int idFilm, int idGenre) {
        String sql = "SELECT EXISTS (SELECT id_film, id_genre " +
                "FROM genre_film " +
                "WHERE id_film = ? AND id_genre = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, idFilm, idGenre);
        return isExist;
    }
}
