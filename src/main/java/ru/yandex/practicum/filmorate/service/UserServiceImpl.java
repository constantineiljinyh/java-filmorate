package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.friend.FriendsStorage;

import java.util.HashSet;
import java.util.List;


@Transactional
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final Storage<User> userStorage;
    private final FriendsStorage friendsStorage;

    public UserServiceImpl(Storage<User> userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
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
        if (!userStorage.isExist(updatedUser.getId())) {
            log.debug("Не корректный пользователь с email {}.", updatedUser.getLogin());
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        }

        validateUser(updatedUser);
        User updated = userStorage.update(updatedUser);
        log.info("Пользователь обновлен: {}", updated);
        return updated;
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

        userStorage.update(user);

        friendsStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            throw new ValidationException("Пользователи не являются друзьями");
        }

        log.info("Удаление пользователя с ID {} из друзей пользователя с ID {}", friendId, userId);
        user.getFriends().remove(friendId);

        userStorage.update(user);

        friendsStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriendsList(Integer userId) {
        userStorage.getById(userId);
        log.info("Получение списка друзей для пользователя с ID {}", userId);
        return friendsStorage.getFriendsList(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        userStorage.getById(userId);
        log.info("Получение списка общих друзей для пользователей с ID {} и {}", userId, otherUserId);
        return friendsStorage.getCommonFriends(userId, otherUserId);
    }

    public User remove(Integer userId) {
        log.info("Удаление пользователя с ID {} ", userId);
        return userStorage.remove(userId);
    }

    @Override
    public boolean isExist(Integer userId) {
        return userStorage.isExist(userId);
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