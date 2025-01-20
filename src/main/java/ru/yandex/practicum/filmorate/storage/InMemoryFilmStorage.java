package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> allFilms = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return allFilms.values();
    }

    @Override
    public Film addFilm(Film postFilm) {
        long id = getNextId();
        postFilm.setId(id);
        allFilms.put(postFilm.getId(), postFilm);
        log.info("Фильм добавлен в коллекцию: " + postFilm);
        return postFilm;
    }

    @Override
    public Film updateFilm(Film putFilm) {
        allFilms.put(putFilm.getId(), putFilm);
        log.info("Фильм обновлен в коллекции: " + putFilm);
        return putFilm;
    }

    @Override
    public void setAllFilmsMap(Map<Long, Film> allFilmsMap) {
        allFilms = allFilmsMap;
    }

    private long getNextId() {
        long currentMaxId = allFilms.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Map<Long, Film> getAllFilmsMap() {
        return allFilms;
    }

    @Override
    public Film getFilm(Long id) {
        Film film = allFilms.get(id);
        if (film == null) {
            log.info("Нет фильма с таким id: " + id);
            throw new UserNotFoundException("Нет фильма с таким id: " + id);
        }
        return film;
    }
}