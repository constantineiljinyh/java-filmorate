package ru.yandex.practicum.filmorate.repository.rating;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

@Component
@AllArgsConstructor
public class RatingMPARepository {

    private final JdbcTemplate jdbcTemplate;

    public List<RatingMPA> getAll() {
        return jdbcTemplate.query("SELECT * FROM ratingMPA", (rs, rowNum) ->
                new RatingMPA(rs.getInt("id_ratingMPA"), rs.getString("name_mpa")));
    }

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
