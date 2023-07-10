package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final Map<Integer, User> usersMap = new HashMap<>();
    private int idUser = 1;

    public User addUser(User user) {
        validateUser(user);
        user.setId(idUser++);
        usersMap.put(user.getId(), user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersMap.values());
    }

    public User updateUser(User updatedUser) {
        validateUser(updatedUser);
        if (usersMap.containsKey(updatedUser.getId())) {
            usersMap.put(updatedUser.getId(), updatedUser);
            return updatedUser;
        } else {
            throw new ValidationException("Такого пользователя нет");
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("Пустой пользователь");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}