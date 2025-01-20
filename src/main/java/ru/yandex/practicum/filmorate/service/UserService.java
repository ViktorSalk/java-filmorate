package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User postUser) {
        return userStorage.addUser(postUser);
    }

    public User updateUser(User putUser) {
        return userStorage.updateUser(putUser);
    }

    public void addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.error("Id пользователя и id друга совпадают");
            throw new ValidationException("Id пользователя и id друга совпадают");
        }
        User user = userStorage.getUser(id);
        if (user == null) {
            log.error("Пользователь с id {} не найден", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }

        User friend = userStorage.getUser(friendId);
        if (friend == null) {
            log.error("Пользователь с id {} не найден", friendId);
            throw new UserNotFoundException("Пользователь с id " + friendId + " не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
        log.info("Друг добавлен с id: " + friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if (user == null || friend == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
        log.info("Друг удален с id: " + friendId);
    }

    public Collection<User> allIdFriends(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            log.error("Пользователь с id {} не найден", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public Collection<User> generalFriends(Long id, Long otherId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);

        if (user == null || otherUser == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : commonFriendIds) {
            User friend = userStorage.getUser(friendId);
            if (friend != null) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }

    public Map<Long, User> getAllUserMap() {
        return userStorage.getCollectionAllUsers();
    }
}
