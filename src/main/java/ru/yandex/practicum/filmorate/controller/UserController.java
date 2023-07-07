package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> usersMap = new HashMap<>();
    private int idUser = 1;

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        try {
            validateUser(user);
            user.setId(idUser++);
            usersMap.put(user.getId(), user);
            log.info("Пользователь создан: {}", user);
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка добавления пользователя: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NullPointerException e) {
            log.error("Ошибка добавления пользователя: Передан пустой пользователь");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка добавления пользователя: Передан некорректный пользователь");
        }
    }

    @GetMapping
    public List<User> getALLUsers() {
        return new ArrayList<>(usersMap.values());
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        try {
            validateUser(updatedUser);
            if (usersMap.containsKey(updatedUser.getId())) {
                usersMap.put(updatedUser.getId(), updatedUser);
                log.info("Пользователь обновлен: {}", updatedUser);
                return updatedUser;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя нет");
            }
        } catch (ValidationException e) {
            log.error("Ошибка обновления пользователя: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
