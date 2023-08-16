package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements Storage<User> {
    private final Map<Integer, User> usersMap = new HashMap<>();
    private int idUser = 1;

    public User add(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пустой пользователь");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        user.setId(idUser++);
        usersMap.put(user.getId(), user);
        return user;
    }

    public List<User> getAll() {
        return new ArrayList<>(usersMap.values());
    }

    public User update(User updatedUser) {
        if (usersMap.containsKey(updatedUser.getId())) {
            usersMap.put(updatedUser.getId(), updatedUser);
            return updatedUser;
        } else {
            throw new ValidationException("Такого пользователя нет");
        }
    }

    public User getById(Integer userId) {
        User user = usersMap.get(userId);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }

    public User remove(Integer userId) {
        User user = usersMap.remove(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userId + " не найден");
        }
        return user;
    }

    @Override
    public boolean isExist(int id) {
        return false;
    }
}