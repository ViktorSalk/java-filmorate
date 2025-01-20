package ru.yandex.practicum.filmorate.controller;

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
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate dayOfCreationCinema = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private FilmService filmService;

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Получен фильм для добавления: {}", film);
        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Фильм {} успешно добавлен", film);

        return film;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.error("Не введен Id фильма");
            throw new ValidationException("id фильма не может быть пустым");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());

            oldFilm.setName(film.getName());
            log.info("Название фильма {} изменено", oldFilm);
            oldFilm.setDescription(film.getDescription());
            log.info("Описание фильма {} изменено", oldFilm);
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.info("Дата выхода фильма {} изменена", oldFilm);
            oldFilm.setDuration(film.getDuration());
            log.info("Длительность фильма {} изменена", oldFilm);

            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", film.getId());
        throw new UserNotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long userId, @PathVariable long id) {
        log.info("Добавлен лайк пользователем с id: " + userId + ". К фильму с id: " + id);
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long userId, @PathVariable long id) {
        filmService.deleteLike(userId, id);
        log.info("Удален лайк пользователем с id: " + userId + ". К фильму с id: " + id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получен список популярных фильмов");
        return filmService.getPopularFilms(count);
    }
}
