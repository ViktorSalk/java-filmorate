package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
            log.info("Id пользователя и id друга совпадают");
            throw new ValidationException("Id пользователя и id друга совпадают");
        }
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        Set<User> userSet = userStorage.getUserFriends(id);
        Set<User> friendSet = userStorage.getUserFriends(friendId);
        if (userSet == null) {
            userSet = new HashSet<>();
        }
        if (friendSet == null) {
            friendSet = new HashSet<>();
        }
        userSet.add(friend);
        friendSet.add(user);
        userStorage.updateUsersFriends(id, userSet);
        userStorage.updateUsersFriends(friendId, friendSet);
        log.info("Друг добавлен с id: " + friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            log.info("Id пользователя и id друга совпадают");
            throw new ValidationException("Id пользователя и id друга совпадают");
        }
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        Set<User> userSet = userStorage.getUserFriends(id);
        Set<User> friendSet = userStorage.getUserFriends(id);
        if (userSet == null) {
            userStorage.updateUsersFriends(user.getId(), null);
            log.info("нет списка друзей");
        }
        if (friendSet == null) {
            log.info("нет списка друзей");
            userStorage.updateUsersFriends(friend.getId(), null);
            return;
        }
        for (User user1 : userSet) {
            if (user1.getId() == friendId) {
                userSet.remove(friend);
                friendSet.remove(user);
                userStorage.updateUsersFriends(id, userSet);
                userStorage.updateUsersFriends(friendId, friendSet);
                log.info("Пользователь с id: " + friendId + "удален из списка");
                return;
            }
        }
        log.info("Пользователь с id: " + friendId + "Нет в списке id");
    }

    public Collection<User> allIdFriends(Long id) {
        User user = userStorage.getUser(id);
        Set<User> friendsSet = userStorage.getUserFriends(id);
        if (friendsSet == null) {
            friendsSet = new HashSet<>();
        }
        return friendsSet;
    }

    public Collection<User> generalFriends(Long id, Long otherId) {
        Collection<User> generalFriends = new ArrayList<>();
        Set<User> users = userStorage.getUserFriends(id);
        Set<User> otherUsers = userStorage.getUserFriends(otherId);
        for (User user : users) {
            for (User otherUser : otherUsers) {
                if (user.getId() == otherUser.getId()) {
                    generalFriends.add(otherUser);
                    break;
                }
            }
        }
        log.info("Возврат списка общих друзей");
        return generalFriends;
    }

    public Map<Long, User> getAllUserMap() {
        return userStorage.getCollectionAllUsers();
    }
}