package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film postFilm) {
        return filmStorage.addFilm(postFilm);
    }

    public Film updateFilm(Film film) {
        filmStorage.getFilm(film.getId()); // проверяем наличие фильма
        return filmStorage.updateFilm(film);
    }

    private void validateFilmAndUser(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            log.error("Фильм с id: {} не найден", filmId);
            throw new UserNotFoundException("Фильм с id: " + filmId + " не найден");
        }

        User user = userStorage.getUser(userId);
        if (user == null) {
            log.error("Пользователь с id: {} не найден", userId);
            throw new UserNotFoundException("Пользователь с id: " + userId + " не найден");
        }
    }

    public void addLike(Long userId, Long id) {
        validateFilmAndUser(id, userId);
        Film film = filmStorage.getFilm(id);
        film.getIdUserLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь с id: {} добавил лайк фильму {}", userId, id);
    }

    public void deleteLike(Long userId, Long id) {
        validateFilmAndUser(id, userId);
        Film film = filmStorage.getFilm(id);
        film.getIdUserLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Пользователь c id: {} удалил лайк у фильма id: {}", userId, id);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());
        films.sort((film, filmToCompare) -> Integer.compare(
                filmToCompare.getIdUserLikes().size(),
                film.getIdUserLikes().size()
        ));
        return films.subList(0, Math.min(count, films.size()));
    }
}