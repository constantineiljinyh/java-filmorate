package ru.yandex.practicum.filmorate.repository.genre;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;

@Component
@AllArgsConstructor
public class GenreFilmRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAll() {
        String selectSql = "SELECT id_genre, name_genre FROM genre";
        return jdbcTemplate.query(selectSql, (rs, rowNum) ->
                new Genre(rs.getInt("id_genre"), rs.getString("name_genre")));
    }

    public Genre getById(Integer genreId) {
        String selectSql = "SELECT id_genre, name_genre FROM genre WHERE id_genre = ?";
        return jdbcTemplate.queryForObject(selectSql, (rs, rowNum) ->
                new Genre(rs.getInt("id_genre"), rs.getString("name_genre")), genreId);
    }

    public LinkedHashSet<Genre> getForFilm(Integer filmId) {
        String selectSql = "SELECT g.id_genre, g.name_genre FROM genre g " +
                "JOIN genre_film gf ON g.id_genre = gf.id_genre " +
                "WHERE gf.id_film = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(selectSql, (rs, rowNum) ->
                new Genre(rs.getInt("id_genre"), rs.getString("name_genre")), filmId));
    }

    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT id_genre FROM genre g WHERE id_genre = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }
}

