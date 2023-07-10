package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        UserService userService = new UserService();
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
}