package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> allFilms = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(allFilms.values());
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

    private long getNextId() {
        long currentMaxId = allFilms.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
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