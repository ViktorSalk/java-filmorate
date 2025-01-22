package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> allUsers = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User addUser(User postUser) {
        long id = getNextId();
        postUser.setId(id);
        allUsers.put(postUser.getId(), postUser);
        log.info("Юзер добавлен в коллекцию: " + postUser);
        return postUser;
    }

    @Override
    public User updateUser(User putUser) {
        allUsers.put(putUser.getId(), putUser);
        return putUser;
    }

    @Override
    public User getUser(long id) {
        User user = allUsers.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    private long getNextId() {
        long currentMaxId = allUsers.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}