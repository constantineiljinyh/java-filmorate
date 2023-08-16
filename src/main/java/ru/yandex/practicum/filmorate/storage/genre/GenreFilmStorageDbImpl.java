package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.ClassificationStorage;

import java.util.List;

@Component
public class GenreFilmStorageDbImpl implements ClassificationStorage<Genre> {

    private final JdbcTemplate jdbcTemplate;

    public GenreFilmStorageDbImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        String selectSql = "SELECT id_genre, name_genre FROM genre";
        return jdbcTemplate.query(selectSql, (rs, rowNum) ->
                new Genre(rs.getInt("id_genre"), rs.getString("name_genre")));
    }

    @Override
    public Genre getById(Integer genreId) {
        String selectSql = "SELECT id_genre, name_genre FROM genre WHERE id_genre = ?";
        return jdbcTemplate.queryForObject(selectSql, (rs, rowNum) ->
                new Genre(rs.getInt("id_genre"), rs.getString("name_genre")), genreId);
    }

    public List<Genre> getForFilm(Integer filmId) {
        String selectSql = "SELECT g.id_genre, g.name_genre FROM genre g " +
                "JOIN genre_film gf ON g.id_genre = gf.id_genre " +
                "WHERE gf.id_film = ?";
        return jdbcTemplate.query(selectSql, (rs, rowNum) ->
                new Genre(rs.getInt("id_genre"), rs.getString("name_genre")), filmId);
    }

    @Override
    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT id_genre FROM genre g WHERE id_genre = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }
}

