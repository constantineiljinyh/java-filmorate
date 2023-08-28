package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.friend.FriendsRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;
    private final FriendsRepository friendsStorage;

    @Override
    @Transactional
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

    @Override
    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userStorage.getAll();
    }

    @Override
    @Transactional
    public User updateUser(User updatedUser) {
        if (!userStorage.isExist(updatedUser.getId())) {
            log.debug("Пользователь не существует {}.", updatedUser.getId());
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        }

        validateUser(updatedUser);
        User updated = userStorage.update(updatedUser);
        log.info("Пользователь обновлен: {}", updated);
        return updated;
    }

    @Override
    public User getUserById(Integer userId) {
        log.info("Получение пользователя по ID: {}", userId);
        return userStorage.getById(userId);
    }

    @Override
    @Transactional
    public void addFriend(Integer userId, Integer friendId) {
        getUserById(userId);
        getUserById(friendId);
        boolean areFriends = friendsStorage.areFriends(userId, friendId);

        if (areFriends) {
            log.debug("Попытка повторного добавления пользователя с ID {} в друзья пользователю с ID {}", friendId, userId);
            throw new ValidationException("Пользователи уже являются друзьями");
        }

        log.info("Добавление пользователя с ID {} в друзья пользователю с ID {}", friendId, userId);

        friendsStorage.addFriend(userId, friendId);
    }

    @Override
    @Transactional
    public void removeFriend(Integer userId, Integer friendId) {
        boolean areFriends = friendsStorage.areFriends(userId, friendId);

        if (!areFriends) {
            log.debug("Пользователи не друзья. Попытка удаления пользователя с ID {} из друзей с ID {}", friendId, userId);
            throw new ValidationException("Пользователи не являются друзьями");
        }

        log.info("Удаление пользователя с ID {} из друзей пользователя с ID {}", friendId, userId);

        friendsStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriendsList(Integer userId) {
        userStorage.getById(userId);
        log.info("Получение списка друзей для пользователя с ID {}", userId);
        return friendsStorage.getFriendsList(userId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        userStorage.getById(userId);
        log.info("Получение списка общих друзей для пользователей с ID {} и {}", userId, otherUserId);
        return friendsStorage.getCommonFriends(userId, otherUserId);
    }

    @Override
    @Transactional
    public User remove(Integer userId) {
        if (userStorage.getById(userId) == null) {
            log.debug("Пользователь равен null id {}", userId);
            throw new NotFoundException("Пользователь равен null");
        }
        log.info("Удаление пользователя с ID {} ", userId);
        return userStorage.remove(userId);
    }

    @Override
    public boolean isExist(Integer userId) {
        return userStorage.isExist(userId);
    }

    private void validateUser(User user) {
        if (user == null) {
            log.debug("Пользователь равен null.");
            throw new ValidationException("Пустой пользователь");
        }
        log.info("Поступил пользователь без имени.");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}