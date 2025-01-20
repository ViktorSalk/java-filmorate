package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(filmService.getAllFilms());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Получен фильм для добавления: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Не введен Id фильма");
            throw new ValidationException("id фильма не может быть пустым");
        }
        if (!filmService.getAllMapFilms().containsKey(film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new UserNotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(userId, id);
        log.info("Добавлен лайк пользователем с id: {} к фильму с id: {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(userId, id);
        log.info("Удален лайк пользователем с id: {} к фильму с id: {}", userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получен список популярных фильмов");
        return filmService.getPopularFilms(count);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final UserNotFoundException e) {
        return Map.of("error", e.getMessage());
    }
}
