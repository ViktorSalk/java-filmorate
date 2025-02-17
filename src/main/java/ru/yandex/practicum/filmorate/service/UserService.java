package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        get(user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public User get(Long id) {
        return userStorage.get(id);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.get(userId);
        userStorage.get(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.get(userId);
        userStorage.get(friendId);
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        userStorage.get(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        userStorage.get(userId);
        userStorage.get(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }
}