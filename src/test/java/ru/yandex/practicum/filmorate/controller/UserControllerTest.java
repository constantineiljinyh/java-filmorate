package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUP() {
        userController = new UserController();
    }

    @Test
    void addUser_EmptyEmail() {
        User userWithEmptyEmail = new User();
        userWithEmptyEmail.setLogin("user123");
        userWithEmptyEmail.setName("Name");
        userWithEmptyEmail.setBirthday(LocalDate.of(1990, 5, 15));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.addUser(userWithEmptyEmail));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getReason());
    }

    @Test
    void addUser_InvalidEmailFormat() {
        User userWithInvalidEmail = new User();
        userWithInvalidEmail.setLogin("user123");
        userWithInvalidEmail.setName("Name");
        userWithInvalidEmail.setBirthday(LocalDate.of(1990, 5, 15));
        userWithInvalidEmail.setEmail("blabla");

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.addUser(userWithInvalidEmail));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getReason());
    }

    @Test
    void addUser_EmptyLogin() {
        User userWithEmptyLogin = new User();
        userWithEmptyLogin.setEmail("user@example.com");
        userWithEmptyLogin.setName("Name");
        userWithEmptyLogin.setBirthday(LocalDate.of(1990, 5, 15));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.addUser(userWithEmptyLogin));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Логин не может быть пустым и содержать пробелы", exception.getReason());
    }

    @Test
    void addUser_LoginWithSpaces() {
        User userWithSpacesInLogin = new User();
        userWithSpacesInLogin.setEmail("user@example.com");
        userWithSpacesInLogin.setLogin("user 123");
        userWithSpacesInLogin.setName("Name");
        userWithSpacesInLogin.setBirthday(LocalDate.of(1990, 5, 15));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.addUser(userWithSpacesInLogin));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Логин не может быть пустым и содержать пробелы", exception.getReason());
    }

    @Test
    void addUser_EmptyName() {
        User userWithEmptyName = new User();
        userWithEmptyName.setEmail("user@example.com");
        userWithEmptyName.setLogin("user123");
        userWithEmptyName.setBirthday(LocalDate.of(1990, 5, 15));

        User createdUser = userController.addUser(userWithEmptyName);

        Assertions.assertEquals("user123", createdUser.getName());
    }

    @Test
    void addUser_NullUser() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.addUser(null));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Ошибка добавления пользователя: Передан некорректный пользователь", exception.getReason());
    }

    @Test
    void addUser_FutureBirthday() {
        User userWithFutureBirthday = new User();
        userWithFutureBirthday.setEmail("user@example.com");
        userWithFutureBirthday.setLogin("user123");
        userWithFutureBirthday.setName("Name");
        userWithFutureBirthday.setBirthday(LocalDate.now().plusDays(1));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                userController.addUser(userWithFutureBirthday));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        Assertions.assertEquals("Дата рождения не может быть в будущем", exception.getReason());
    }
    @Test
    void getALLUsers_ReturnsAllUsers() {
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

        List<User> allUsers = userController.getALLUsers();

        Assertions.assertEquals(2, allUsers.size());
        Assertions.assertTrue(allUsers.contains(user1));
        Assertions.assertTrue(allUsers.contains(user2));
    }

    @Test
    void updateUser_ExistingUser() {
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

        List<User> allUsers = userController.getALLUsers();
        Assertions.assertEquals(1, allUsers.size());
        Assertions.assertTrue(allUsers.contains(returnedUser));
    }

    @Test
    void updateUser_NonExistingUser() {
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