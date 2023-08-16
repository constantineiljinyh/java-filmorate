package ru.yandex.practicum.filmorate.storage.user;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary

public class UserStorageDbImpl implements Storage<User> {

    private final JdbcTemplate jdbcTemplate;

    public UserStorageDbImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday, f.id_friend " +
                "FROM users u " +
                "LEFT JOIN friends f ON u.id = f.id_user OR u.id = f.id_friend";

        Map<Integer, User> userMap = new HashMap<>();

        jdbcTemplate.query(sql, rs -> {
            int userId = rs.getInt("id");

            User user = userMap.get(userId);

            if (user == null) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("login"),
                        rs.getDate("birthday").toLocalDate(),
                        new HashSet<>()
                );

                userMap.put(userId, user);
            }

            int friendId = rs.getInt("id_friend");
            if (friendId != 0) {
                user.getFriends().add(friendId);
            }

        });

        return new ArrayList<>(userMap.values());
    }

    @Override
    public User update(User updateUser) {
        String checkUserSql = "SELECT id FROM users WHERE id = ?";
        List<Integer> existingUserIds = jdbcTemplate.queryForList(checkUserSql, Integer.class, updateUser.getId());

        if (existingUserIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя нет");
        }

        String updateUserSql = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE id = ?";
        String updateFriendSql = "UPDATE friends SET id_user = ?, id_friend = ? WHERE id_user = ? AND id_friend = ?";

        jdbcTemplate.update(updateUserSql,
                updateUser.getName(),
                updateUser.getEmail(),
                updateUser.getLogin(),
                Timestamp.valueOf(updateUser.getBirthday().atStartOfDay()),
                updateUser.getId());

        Set<Integer> friends = updateUser.getFriends();
        if (friends != null) {
            List<Object[]> batchArgs = friends.stream()
                    .map(friendId -> new Object[]{updateUser.getId(), friendId, friendId, updateUser.getId()})
                    .collect(Collectors.toList());

            jdbcTemplate.batchUpdate(updateFriendSql, batchArgs);
        }

        return updateUser;
    }

    @Override
    public User getById(Integer userId) {
        String userExistsSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        int userCount = jdbcTemplate.queryForObject(userExistsSql, Integer.class, userId);

        if (userCount == 0) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday, f.id_friend " +
                "FROM users u " +
                "LEFT JOIN friends f ON u.id = f.id_user OR u.id = f.id_friend " +
                "WHERE u.id = ?";

        return jdbcTemplate.query(sql, rs -> {
            User currentUser = null;

            while (rs.next()) {
                if (currentUser == null) {
                    currentUser = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getDate("birthday").toLocalDate(),
                            new HashSet<>()
                    );
                }

                int friendId = rs.getInt("id_friend");
                if (friendId != 0) {
                    currentUser.getFriends().add(friendId);
                }
            }

            if (currentUser == null) {
                throw new NotFoundException("Пользователь с ID " + userId + " не найден");
            }

            return currentUser;
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
