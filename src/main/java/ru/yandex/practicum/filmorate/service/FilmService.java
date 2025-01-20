package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.LikeComparator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final LikeComparator likeComparator = new LikeComparator();

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film postFilm) {
        return filmStorage.addFilm(postFilm);
    }

    public Film updateFilm(Film putFilm) {
        return filmStorage.updateFilm(putFilm);
    }

    public void addLike(Long userId, Long id) {
        Film film = Optional.ofNullable(filmStorage.getFilm(id))
                .orElseThrow(() -> new UserNotFoundException("Фильм с id: " + id + " не найден"));
        User user = Optional.ofNullable(userStorage.getUser(userId))
                .orElseThrow(() -> new UserNotFoundException("Фильм с id: " + userId + " не найден"));

        film.getIdUserLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь с id: {} добавил лайк фильму {}", userId, id);
    }

    public void deleteLike(Long userId, Long id) {
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);

        film.getIdUserLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь c id: {} удалил лайк у фильма id: {}", userId, id);
    }

    public Collection<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());
        films.sort((f1, f2) -> Integer.compare(
                f2.getIdUserLikes().size(),
                f1.getIdUserLikes().size()
        ));
        return films.subList(0, Math.min(count, films.size()));
    }

    public Map<Long, Film> getAllMapFilms() {
        return filmStorage.getAllFilmsMap();
    }
}