package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private UserController userController;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User to Remove");
        user1.setEmail("user@example.com");
        user1.setLogin("user123");
        user1.setBirthday(LocalDate.of(1990, 5, 15));

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user456");
        user2.setName("Name 2");
        user2.setBirthday(LocalDate.of(1995, 8, 20));
    }

    @Test
    void addUserEmptyName() {
        User userWithEmptyName = new User();
        userWithEmptyName.setEmail("user@example.com");
        userWithEmptyName.setLogin("user123");
        userWithEmptyName.setBirthday(LocalDate.of(1990, 5, 15));

        User createdUser = userController.addUser(userWithEmptyName);

        Assertions.assertEquals("user123", createdUser.getName());
    }

    @Test
    void removeUserValidUserId() {
        User createdUser = userController.addUser(user1);
        Integer userId = createdUser.getId();

        User removedUser = userController.removeUser(userId);

        Assertions.assertEquals(createdUser, removedUser);
    }

    @Test
    void removeUserInvalidUserId() {
        Integer nonExistentUserId = 1000;

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                userController.removeUser(nonExistentUserId));

        Assertions.assertEquals("Пользователь с ID " + nonExistentUserId + " не найден", exception.getMessage());
    }

    @Test
    void getALLUsersReturnsAllUsers() {
        userController.addUser(user1);
        userController.addUser(user2);

        List<User> allUsers = userController.getAllUsers();

        Assertions.assertEquals(2, allUsers.size());
    }

    @Test
    void updateUserExistingUser() {
        User createdUser = userController.addUser(user1);

        User updatedUser = new User();
        updatedUser.setId(createdUser.getId());
        updatedUser.setEmail("updated@example.com");
        updatedUser.setLogin("updated123");
        updatedUser.setName("Updated Name");
        updatedUser.setBirthday(LocalDate.of(1995, 8, 20));

        User returnedUser = userController.updateUser(updatedUser);

        Assertions.assertEquals(updatedUser.getId(), returnedUser.getId());
        Assertions.assertEquals(updatedUser.getEmail(), returnedUser.getEmail());
        Assertions.assertEquals(updatedUser.getLogin(), returnedUser.getLogin());
        Assertions.assertEquals(updatedUser.getName(), returnedUser.getName());
        Assertions.assertEquals(updatedUser.getBirthday(), returnedUser.getBirthday());

        List<User> allUsers = userController.getAllUsers();
        Assertions.assertEquals(1, allUsers.size());
    }

    @Test
    void addFriendValidUsers() {
        userController.addUser(user1);
        userController.addUser(user2);

        userController.addFriend(1, 2);

        List<User> friendsList = userController.getFriendsList(1);
        Assertions.assertEquals(1, friendsList.size());
    }

    @Test
    void addFriendAlreadyFriends() {
        userController.addUser(user1);
        userController.addUser(user2);

        userController.addFriend(1, 2);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                userController.addFriend(1, 2));

        Assertions.assertEquals("Пользователи уже являются друзьями", exception.getMessage());
    }

    @Test
    void removeFriendValidUsers() {
        userController.addUser(user1);
        userController.addUser(user2);

        userController.addFriend(1, 2);

        userController.removeFriend(1, 2);

        List<User> friendsList = userController.getFriendsList(1);
        Assertions.assertEquals(0, friendsList.size());
    }

    @Test
    void removeFriendNotFriends() {
        userController.addUser(user1);
        userController.addUser(user2);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                userController.removeFriend(1, 2));

        Assertions.assertEquals("Пользователи не являются друзьями", exception.getMessage());
    }

    @Test
    void getFriendsListValidUser() {
        userController.addUser(user1);
        userController.addUser(user2);

        userController.addFriend(1, 2);

        List<User> friendsList = userController.getFriendsList(1);
        Assertions.assertEquals(1, friendsList.size());
    }

    @Test
    void getFriendsListInvalidUser() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                userController.getFriendsList(100));

        Assertions.assertEquals("Пользователь с ID " + 100 + " не найден", exception.getMessage().trim());
    }

    @Test
    void getCommonFriendsValidUsers() {
        userController.addUser(user1);
        userController.addUser(user2);

        User user3 = new User();
        user3.setEmail("user2@exaample.com");
        user3.setLogin("user452");
        user3.setName("Name 3");
        user3.setBirthday(LocalDate.of(1991, 8, 20));
        userController.addUser(user3);

        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);

        List<User> commonFriends = userController.getCommonFriends(1, 2);
        Assertions.assertEquals(1, commonFriends.size());
    }

    @Test
    void getCommonFriendsNoCommonFriends() {
        userController.addUser(user1);
        userController.addUser(user2);

        User user3 = new User();
        user3.setEmail("user2@exaample.com");
        user3.setLogin("user452");
        user3.setName("Name 3");
        user3.setBirthday(LocalDate.of(1991, 8, 20));
        userController.addUser(user3);

        List<User> commonFriends = userController.getCommonFriends(1, 2);
        Assertions.assertEquals(0, commonFriends.size());
    }

    @Test
    void getCommonFriendsInvalidUser() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                userController.getCommonFriends(100, 2));

        Assertions.assertEquals("Пользователь с ID " + 100 + " не найден", exception.getMessage());
    }
}

