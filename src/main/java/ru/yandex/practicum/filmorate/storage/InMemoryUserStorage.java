package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> allUsers = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User create(User user) {
        long id = getNextId();
        user.setId(id);
        allUsers.put(user.getId(), user);
        log.info("User added to collection: " + user);
        return user;
    }

    @Override
    public User update(User user) {
        allUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Long id) {
        User user = allUsers.get(id);
        if (user == null) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = get(userId);
        User friend = get(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = get(userId);
        return user.getFriends().stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = get(userId);
        User otherUser = get(otherId);

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());

        return commonFriendIds.stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean exists(Long userId) {
        return allUsers.containsKey(userId);
    }

    private long getNextId() {
        long currentMaxId = allUsers.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}