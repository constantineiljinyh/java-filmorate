package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.ClassificationStorage;

import java.util.List;

@Component
public class RatingMPAStorageDbImpl implements ClassificationStorage<RatingMPA> {

    private final JdbcTemplate jdbcTemplate;

    public RatingMPAStorageDbImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RatingMPA> getAll() {
        return jdbcTemplate.query("SELECT * FROM ratingMPA", (rs, rowNum) ->
                new RatingMPA(rs.getInt("id_ratingMPA"), rs.getString("name_mpa")));
    }

    @Override
    public RatingMPA getById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM ratingMPA WHERE id_ratingMPA = ?", (rs, rowNum) -> {
            RatingMPA ratingMpa = new RatingMPA();
            ratingMpa.setId(rs.getInt("id_ratingMPA"));
            ratingMpa.setName(rs.getString("name_MPA"));
            return ratingMpa;
        }, id);
    }

    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT id_ratingMPA FROM ratingMPA WHERE id_ratingMPA = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }
}
