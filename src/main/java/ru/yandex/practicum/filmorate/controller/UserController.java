package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        log.debug("Поступил запрос на добавление пользователя с id {}.", user.getId());
        return userService.addUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("Поступил запрос на просмотр всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        log.debug("Поступил запрос на просмотр пользователя с id {}.", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{id}")
    public User removeUser(@PathVariable("id") Integer userId) {
        log.debug("Поступил запрос на удаление пользователя с id {}.", userId);
        return userService.remove(userId);
    }

    @PutMapping
    public User updateUser(@RequestBody User updatedUser) {
        log.debug("Поступил запрос на обновление пользователя с id {}.", updatedUser.getId());
        return userService.updateUser(updatedUser);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@PathVariable("id") Integer userId) {
        log.debug("Поступил запрос на получение списка друзей для пользователя с id {}.", userId);
        return userService.getFriendsList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer otherUserId) {
        log.debug("Поступил запрос на получение общих друзей для пользователей с id {} и {}.", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        log.debug("Поступил запрос на добавление друга для пользователя с id {}.", userId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        log.debug("Поступил запрос на удаление друга для пользователя с id {}.", userId);
        userService.removeFriend(userId, friendId);
    }
}
