package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> usersMap = new HashMap<>();
    private int idUser = 1;

    public User addUser(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        user.setId(idUser++);
        usersMap.put(user.getId(), user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersMap.values());
    }

    public User updateUser(User updatedUser) {
        if (usersMap.containsKey(updatedUser.getId())) {
            usersMap.put(updatedUser.getId(), updatedUser);
            return updatedUser;
        } else {
            throw new ValidationException("Такого пользователя нет");
        }
    }

    public User getUserById(Integer userId) {
        User user = usersMap.get(userId);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }
}
