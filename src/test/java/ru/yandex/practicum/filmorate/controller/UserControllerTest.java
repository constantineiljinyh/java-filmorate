package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

class UserControllerTest {
    private UserController userController;

    private UserService userService;

    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new InMemoryUserService(userStorage);
        userController = new UserController(userService);
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
    void addUserNullUser() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.addUser(null));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Пустой пользователь", exception.getReason());
    }

    @Test
    void getALLUsersReturnsAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user123");
        user1.setName("Name 1");
        user1.setBirthday(LocalDate.of(1990, 5, 15));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user456");
        user2.setName("Name 2");
        user2.setBirthday(LocalDate.of(1995, 8, 20));
        userController.addUser(user2);

        List<User> allUsers = userController.getAllUsers();

        Assertions.assertEquals(2, allUsers.size());
        Assertions.assertTrue(allUsers.contains(user1));
        Assertions.assertTrue(allUsers.contains(user2));
    }

    @Test
    void updateUserExistingUser() {
        User existingUser = new User();
        existingUser.setEmail("user@example.com");
        existingUser.setLogin("user123");
        existingUser.setName("Name");
        existingUser.setBirthday(LocalDate.of(1990, 5, 15));
        User createdUser = userController.addUser(existingUser);

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
        Assertions.assertTrue(allUsers.contains(returnedUser));
    }

    @Test
    void updateUserNonExistingUser() {
        User nonExistingUser = new User();
        nonExistingUser.setId(123);
        nonExistingUser.setEmail("user@example.com");
        nonExistingUser.setLogin("user123");
        nonExistingUser.setName("Name");
        nonExistingUser.setBirthday(LocalDate.of(1990, 5, 15));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.updateUser(nonExistingUser));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        Assertions.assertEquals("Такого пользователя нет", exception.getReason());
    }

    @Test
    void addFriendValidUsers() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        userStorage.addUser(user2);

        userController.addFriend(1, 2);

        List<User> friendsList = userController.getFriendsList(1);
        Assertions.assertEquals(1, friendsList.size());
        Assertions.assertEquals(user2, friendsList.get(0));
    }

    @Test
    void addFriendAlreadyFriends() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        userStorage.addUser(user2);

        userController.addFriend(1, 2);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                userController.addFriend(1, 2));

        Assertions.assertEquals("Пользователи уже являются друзьями", exception.getMessage());
    }

    @Test
    void removeFriendValidUsers() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        userStorage.addUser(user2);

        userController.addFriend(1, 2);

        userController.removeFriend(1, 2);

        List<User> friendsList = userController.getFriendsList(1);
        Assertions.assertEquals(0, friendsList.size());
    }

    @Test
    void removeFriendNotFriends() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        userStorage.addUser(user2);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                userController.removeFriend(1, 2));

        Assertions.assertEquals("Пользователи не являются друзьями", exception.getMessage());
    }

    @Test
    void getFriendsListValidUser() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        userStorage.addUser(user2);

        userController.addFriend(1, 2);

        List<User> friendsList = userController.getFriendsList(1);
        Assertions.assertEquals(1, friendsList.size());
        Assertions.assertEquals(user2, friendsList.get(0));
    }

    @Test
    void getFriendsListInvalidUser() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                userController.getFriendsList(1));

        Assertions.assertEquals("Пользователь с ID 1 не найден", exception.getMessage());
    }

    @Test
    void getCommonFriendsValidUsers() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        userStorage.addUser(user2);

        User user3 = new User();
        user3.setId(3);
        user3.setName("User 3");
        userStorage.addUser(user3);

        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);

        List<User> commonFriends = userController.getCommonFriends(1, 2);
        Assertions.assertEquals(1, commonFriends.size());
        Assertions.assertEquals(user3, commonFriends.get(0));
    }

    @Test
    void getCommonFriendsNoCommonFriends() {
        User user1 = new User();
        user1.setId(1);
        user1.setName("User 1");
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setName("User 2");
        userStorage.addUser(user2);

        User user3 = new User();
        user3.setId(3);
        user3.setName("User 3");
        userStorage.addUser(user3);

        List<User> commonFriends = userController.getCommonFriends(1, 2);
        Assertions.assertEquals(0, commonFriends.size());
    }

    @Test
    void getCommonFriendsInvalidUser() {
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () ->
                userController.getCommonFriends(1, 2));

        Assertions.assertEquals("Пользователь с ID 1 не найден", exception.getMessage());
    }
}