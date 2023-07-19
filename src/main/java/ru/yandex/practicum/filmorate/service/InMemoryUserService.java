package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InMemoryUserService implements UserService {
    private UserStorage userStorage;

    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User updatedUser) {
        validateUser(updatedUser);
        return userStorage.updateUser(updatedUser);
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
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

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId) || !friend.getFriends().contains(userId)) {
            throw new ValidationException("Пользователи не являются друзьями");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getFriendsList(Integer userId) {
        User user = getUserById(userId);
        List<User> friends = new ArrayList<>();

        for (Integer friendId : user.getFriends()) {
            User friend = getUserById(friendId);
            friends.add(friend);
        }

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

        return commonFriendList;
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