package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate dayOfCreationCinema = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.debug("Получен фильм для добавления: {}", film);
        film.setId(getNextId());

        filmValidator(film);

        films.put(film.getId(), film);
        log.info("Фильм {} успешно добавлен", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Не введен Id фильма");
            throw new ValidationException("id фильма не может быть пустым");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            filmValidator(newFilm);

            oldFilm.setName(newFilm.getName());
            log.info("Название фильма {} изменено", oldFilm);
            oldFilm.setDescription(newFilm.getDescription());
            log.info("Описание фильма {} изменено", oldFilm);
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Дата выходи фильма {} изменена", oldFilm);
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Длительность фильма {} изменена", oldFilm);
            return oldFilm;
        }
        log.error("Фильм с id = {} не найден", newFilm.getId());
        throw new UserNotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private void filmValidator(@RequestBody Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не заполнено при добавлении");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма слишком длинное при добавлении");
            throw new ValidationException("Слишком длинное описание");
        }
        if (film.getReleaseDate().isBefore(dayOfCreationCinema)) {
            log.error("Указана неверная дата выходи фильма при добавлении");
            throw new ValidationException("Указана неверная дата");
        }
        if (film.getDuration() < 0) {
            log.error("При добавлении фильма длительность указана меньше 0");
            throw new ValidationException("Длительность фильма не может быть меньше 0");
        }
    }
}
