package ru.yandex.practicum.filmorate.storage.like;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@AllArgsConstructor
public class LikeFilmsStorageDbImpl implements LikeFilmsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String insertSql = "INSERT INTO like_film (id_film, id_user) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        String deleteSql = "DELETE FROM like_film WHERE id_film = ? AND id_user = ?";
        jdbcTemplate.update(deleteSql, filmId, userId);
    }

    @Override
    public void dislikeForFilm(int filmId, int userId) {
        removeLike(filmId, userId);
        minusRateForFilm(filmId);
    }

    @Override
    public int getLikeCount(Integer filmId) {
        String countSql = "SELECT COUNT(*) FROM like_film WHERE id_film = ?";
        return jdbcTemplate.queryForObject(countSql, Integer.class, filmId);
    }

    @Override
    public void minusRateForFilm(int filmId) {
        String updateSql = "UPDATE films SET rating = rating + 1 WHERE id = ?";
        jdbcTemplate.update(updateSql, filmId);
    }

    @Override
    public void likeForFilm(int filmId, int userId) {
        addLike(filmId, userId);
        plusRateForFilm(filmId);
    }

    @Override
    public void plusRateForFilm(int filmId) {
        String updateSql = "UPDATE films SET rating = rating + 1 WHERE id = ?";
        jdbcTemplate.update(updateSql, filmId);
    }

    @Override
    public boolean isExistLike(int filmId, int userId) {
        HashMap<Integer, Integer> likeMap = new HashMap<>();
        String sql = "SELECT * FROM like_film lf WHERE lf.id_film = ? AND lf.id_user = ?";
        jdbcTemplate.query(sql, (rs) -> {
            Integer fetchedFilmId = rs.getInt("ID_FILM");
            Integer fetchedUserId = rs.getInt("id_user");
            if (fetchedFilmId != null && fetchedUserId != null && fetchedFilmId > 0 && fetchedUserId > 0) {
                likeMap.put(fetchedFilmId, fetchedUserId);
            }
        }, filmId, userId);

        if (likeMap.containsKey(filmId)) {
            if (likeMap.get(filmId).equals(userId)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}


