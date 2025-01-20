package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final Set<String> logins = new HashSet<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserService userService;

    private Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        user.setId(getNextId());

        if (logins.contains(user.getLogin())) {
            log.error("Попытка добавить пользователя с существующим логином: {}", user.getLogin());
            throw new ValidationException("Ошибка. Попытка добавить логин, который уже существует");
        }

        if (emails.contains(user.getEmail())) {
            log.error("Попытка добавить пользователя с существующим email: {}", user.getEmail());
            throw new ValidationException("Ошибка. Email уже существует");
        }

        users.put(user.getId(), user);
        emails.add(user.getEmail());
        logins.add(user.getLogin());
        log.info("Пользователь добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Не введен id пользователя при изменении");
            throw new ValidationException("id пользователя должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            // Удаляем старый адрес электронной почты и логин из наборов
            emails.remove(oldUser.getEmail());
            logins.remove(oldUser.getLogin());

            // Проверяем, не конфликтуют ли новые адрес электронной почты и логин
            if (emails.contains(newUser.getEmail())) {
                throw new ValidationException("Email уже существует");
            }
            if (logins.contains(newUser.getLogin())) {
                throw new ValidationException("Логин уже существует");
            }

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());

            // Добавляем новый Email и Логин
            emails.add(newUser.getEmail());
            logins.add(newUser.getLogin());

            return oldUser;
        }
        log.error("Пользователь с id {} не найден", newUser.getId());
        throw new UserNotFoundException("Пользователь с id =" + newUser.getId() + " не найден");
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пошел процесс добавления друга с id:" + friendId + ". Пользователем id:" + id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Пошел процесс удаления друга с id:" + friendId + ". Пользователем id:" + id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> allIdFriends(@PathVariable long id) {
        try {
            return userService.allIdFriends(id);
        } catch (UserNotFoundException e) {
            log.error("Пользователь с id {} не найден", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> generalFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Идет процесс получения общих друзей у пользователя с id: " + id + ". C пользователя с id: " + otherId);
        return userService.generalFriends(id, otherId);
    }
}