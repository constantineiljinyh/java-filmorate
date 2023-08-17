package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Component
@Primary
@AllArgsConstructor
public class UserStorageDbImpl implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пустой пользователь");
        }

        String sql = "INSERT INTO users (name, email, login, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setTimestamp(4, Timestamp.valueOf(user.getBirthday().atStartOfDay()));
            return ps;
        }, keyHolder);

        int generatedId = keyHolder.getKey().intValue();
        user.setId(generatedId);

        return user;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday " +
                "FROM users u";

        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        });

        return users;
    }

    @Override
    public User update(User updateUser) {
        String updateUserSql = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";

        jdbcTemplate.update(updateUserSql,
                updateUser.getName(),
                updateUser.getEmail(),
                updateUser.getLogin(),
                Timestamp.valueOf(updateUser.getBirthday().atStartOfDay()),
                updateUser.getId());

        return updateUser;
    }

    @Override
    public User getById(Integer userId) {
        String userExistsSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        int userCount = jdbcTemplate.queryForObject(userExistsSql, Integer.class, userId);

        if (userCount == 0) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday " +
                "FROM users u " +
                "WHERE u.id = ?";

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }, userId);
    }

    @Override
    public User remove(Integer userId) {
        String getUserByIdSql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(getUserByIdSql, new Object[]{userId}, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        });

        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User userToRemove = users.get(0);

        String deleteSql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteSql, userId);

        return userToRemove;
    }

    @Override
    public boolean isExist(int id) {
        String checkId = "SELECT COUNT(id) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkId, Integer.class, id);
        if (count < 1) {
            return false;
        } else {
            return true;
        }
    }
}
