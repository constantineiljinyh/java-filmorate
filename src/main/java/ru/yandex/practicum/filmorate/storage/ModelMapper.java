package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ModelMapper {
    public static RowMapper<List<Film>> mapperGetFilms() {
        return (rs, rowNum) -> {
            Map<Integer, Film> filmMap = new HashMap<>();

            do {
                int filmId = rs.getInt("id");

                if (!filmMap.containsKey(filmId)) {
                    Film film = new Film();
                    film.setId(filmId);
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));
                    film.setRate(rs.getInt("rating"));
                    film.setGenres(new LinkedHashSet<>());
                    filmMap.put(filmId, film);

                    RatingMPA ratingMpa = new RatingMPA();
                    ratingMpa.setId(rs.getInt("id_ratingMPA"));
                    ratingMpa.setName(rs.getString("name_MPA"));
                    film.setMpa(ratingMpa);
                }

                int genreId = rs.getInt("id_genre");
                if (genreId > 0) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("name_genre"));
                    filmMap.get(filmId).getGenres().add(genre);
                }
            } while (rs.next());

            return new ArrayList<>(filmMap.values());
        };
    }

    public static PreparedStatement createInsertFilmStatement(Connection connection, Film film, Integer[] genreIds) throws SQLException {
        String insertFilmSql = "INSERT INTO films (duration, name, description, rating, release_date, id_ratingMPA) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(insertFilmSql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, film.getDuration());
        ps.setString(2, film.getName());
        ps.setString(3, film.getDescription());
        if (film.getRate() != null) {
            ps.setInt(4, film.getRate());
        } else {
            ps.setNull(4, Types.INTEGER);
        }
        ps.setTimestamp(5, Timestamp.valueOf(film.getReleaseDate().atStartOfDay()));
        ps.setInt(6, film.getMpa().getId());


        return ps;
    }

}
