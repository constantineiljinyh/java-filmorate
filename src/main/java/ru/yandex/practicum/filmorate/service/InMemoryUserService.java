package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class InMemoryUserService implements UserService {
    private Storage<User> userStorage;

    public InMemoryUserService(Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        try {
            validateUser(user);
            User addedUser = userStorage.add(user);
            log.info("Пользователь создан: {}", addedUser);
            return addedUser;
        } catch (ValidationException e) {
            log.error("Ошибка добавления пользователя: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userStorage.getAll();
    }

    public User updateUser(User updatedUser) {
        try {
            validateUser(updatedUser);
            User updated = userStorage.update(updatedUser);
            log.info("Пользователь обновлен: {}", updated);
            return updated;
        } catch (ValidationException e) {
            log.error("Ошибка обновления пользователя: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    public User getUserById(Integer userId) {
        log.info("Получение пользователя по ID: {}", userId);
        return userStorage.getById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        if (user.getFriends().contains(friendId) || friend.getFriends().contains(userId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }

        log.info("Добавление пользователя с ID {} в друзья пользователю с ID {}", friendId, userId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId) || !friend.getFriends().contains(userId)) {
            throw new ValidationException("Пользователи не являются друзьями");
        }

        log.info("Удаление пользователя с ID {} из друзей пользователя с ID {}", friendId, userId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public List<User> getFriendsList(Integer userId) {
        User user = getUserById(userId);
        List<User> friends = new ArrayList<>();

        for (Integer friendId : user.getFriends()) {
            User friend = getUserById(friendId);
            friends.add(friend);
        }
        log.info("Получение списка друзей для пользователя с ID {}", userId);
        return friends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<Integer> commonFriends = new HashSet<>();
        if (user.getFriends() != null && otherUser.getFriends() != null) {
            commonFriends.addAll(user.getFriends());
            commonFriends.retainAll(otherUser.getFriends());
        }

        List<User> commonFriendList = new ArrayList<>();
        for (Integer friendId : commonFriends) {
            User friend = getUserById(friendId);
            if (friend != null) {
                commonFriendList.add(friend);
            }
        }
        log.info("Получение списка общих друзей для пользователей с ID {} и {}", userId, otherUserId);
        return commonFriendList;
    }

    public User remove(Integer userId) {
        log.info("Удаление пользователя с ID {} ", userId);
        return userStorage.remove(userId);
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