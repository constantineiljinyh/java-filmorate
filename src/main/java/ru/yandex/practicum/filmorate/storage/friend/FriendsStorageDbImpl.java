package ru.yandex.practicum.filmorate.storage.friend;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class FriendsStorageDbImpl implements FriendsStorage {

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
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday, " +
                "STRING_AGG(CAST(f.id_friend AS VARCHAR), ', ') as friend_ids " +
                "FROM users u " +
                "INNER JOIN friends f ON u.id = f.id_friend " +
                "WHERE f.id_user = ? " +
                "GROUP BY u.id";

        List<User> friends = jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            User friend = new User();
            friend.setId(rs.getInt("id"));
            friend.setName(rs.getString("name"));
            friend.setEmail(rs.getString("email"));
            friend.setLogin(rs.getString("login"));
            friend.setBirthday(rs.getDate("birthday").toLocalDate());

            String friendIds = rs.getString("friend_ids");
            if (friendIds != null && !friendIds.isEmpty()) {
                Set<Integer> friendIdSet = Arrays.stream(friendIds.split(", "))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());
                friend.setFriends(friendIdSet);
            }

            return friend;
        });

        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday, " +
                "STRING_AGG(CAST(f1.id_friend AS VARCHAR), ', ') as friend_ids " +
                "FROM users u " +
                "INNER JOIN friends f1 ON u.id = f1.id_friend " +
                "INNER JOIN friends f2 ON u.id = f2.id_friend " +
                "WHERE f1.id_user = ? AND f2.id_user = ? " +
                "GROUP BY u.id";
        ;

        List<User> commonFriends = jdbcTemplate.query(sql, new Object[]{userId, otherUserId}, (rs, rowNum) -> {
            User commonFriend = new User();
            commonFriend.setId(rs.getInt("id"));
            commonFriend.setName(rs.getString("name"));
            commonFriend.setEmail(rs.getString("email"));
            commonFriend.setLogin(rs.getString("login"));
            commonFriend.setBirthday(rs.getDate("birthday").toLocalDate());

            String friendIds = rs.getString("friend_ids");
            if (friendIds != null && !friendIds.isEmpty()) {
                Set<Integer> friendIdSet = Arrays.stream(friendIds.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toSet());
                commonFriend.setFriends(friendIdSet);
            }

            return commonFriend;
        });

        return commonFriends;
    }
}
