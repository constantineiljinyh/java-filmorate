package ru.yandex.practicum.filmorate.repository.friend;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendsRepositoryTest {

    @Autowired
    private FriendsRepository friendsStorageDb;

    @Autowired
    private UserRepository userDbStorage;

    @BeforeEach
    public void setUp() {
        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        user2.setLogin("janesmith");
        user2.setBirthday(LocalDate.of(1985, 8, 22));

        userDbStorage.add(user2);

        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        userDbStorage.add(userToAdd);

        User user3 = new User();
        user3.setEmail("user2@exaample.com");
        user3.setLogin("user452");
        user3.setName("Name 3");
        user3.setBirthday(LocalDate.of(1991, 8, 20));
        userDbStorage.add(user3);
    }

    @Test
    void addFriendAndCheckIfAdded() {
        Integer userId = 1;
        Integer friendId = 2;

        friendsStorageDb.addFriend(userId, friendId);

        List<User> friends = friendsStorageDb.getFriendsList(userId);
        assertTrue(friends.stream().anyMatch(friend -> friend.getId().equals(friendId)));
    }

    @Test
    void removeFriendAndCheckIfRemoved() {
        Integer userId = 1;
        Integer friendId = 2;

        friendsStorageDb.addFriend(userId, friendId);
        friendsStorageDb.removeFriend(userId, friendId);

        List<User> friends = friendsStorageDb.getFriendsList(userId);
        assertTrue(friends.stream().noneMatch(friend -> friend.getId().equals(friendId)));
    }

    @Test
    void getCommonFriends() {
        friendsStorageDb.addFriend(1, 2);
        friendsStorageDb.addFriend(1, 3);
        friendsStorageDb.addFriend(2, 3);

        List<User> commonFriends = friendsStorageDb.getCommonFriends(1, 2);
        Assertions.assertEquals(1, commonFriends.size());
    }
}
