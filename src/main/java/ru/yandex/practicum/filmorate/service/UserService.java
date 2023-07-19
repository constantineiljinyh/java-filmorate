package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> getAllUsers();

    User updateUser(User updatedUser);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    List<User> getFriendsList(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherUserId);

    User getUserById(Integer userId);
}
