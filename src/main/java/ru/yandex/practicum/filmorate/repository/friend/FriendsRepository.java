package ru.yandex.practicum.filmorate.repository.friend;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@AllArgsConstructor
public class FriendsRepository  {

    private final JdbcTemplate jdbcTemplate;

    public void addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO friends (id_user, id_friend) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friends WHERE (id_user = ? AND id_friend = ?) OR (id_user = ? AND id_friend = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    public List<User> getFriendsList(Integer userId) {
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday " +
                "FROM users u " +
                "INNER JOIN friends f ON u.id = f.id_friend " +
                "WHERE f.id_user = ?";

        List<User> friends = jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            User friend = new User();
            friend.setId(rs.getInt("id"));
            friend.setName(rs.getString("name"));
            friend.setEmail(rs.getString("email"));
            friend.setLogin(rs.getString("login"));
            friend.setBirthday(rs.getDate("birthday").toLocalDate());
            return friend;
        });

        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday " +
                "FROM users u " +
                "INNER JOIN friends f1 ON u.id = f1.id_friend " +
                "INNER JOIN friends f2 ON u.id = f2.id_friend " +
                "WHERE f1.id_user = ? AND f2.id_user = ?";

        List<User> commonFriends = jdbcTemplate.query(sql, new Object[]{userId, otherUserId}, (rs, rowNum) -> {
            User commonFriend = new User();
            commonFriend.setId(rs.getInt("id"));
            commonFriend.setName(rs.getString("name"));
            commonFriend.setEmail(rs.getString("email"));
            commonFriend.setLogin(rs.getString("login"));
            commonFriend.setBirthday(rs.getDate("birthday").toLocalDate());
            return commonFriend;
        });

        return commonFriends;
    }

    public boolean areFriends(Integer userId, Integer friendId) {
        String sql = "SELECT COUNT(*) FROM friends WHERE (id_user = ? AND id_friend = ?) OR (id_user = ? AND id_friend = ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId, friendId, userId);
        return count != null && count > 0;
    }
}
