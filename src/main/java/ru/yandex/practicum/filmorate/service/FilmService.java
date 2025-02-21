package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreDto;
import ru.yandex.practicum.filmorate.model.MpaDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film createFilm(Film film) {
        validateMpaAndGenres(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateMpaAndGenres(film);
        get(film.getId());
        return filmStorage.update(film);
    }

    public Film get(Long id) {
        return filmStorage.get(id);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.get(filmId);
        userStorage.get(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmStorage.get(filmId);
        userStorage.get(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopular(count);
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() < 1 || film.getMpa().getId() > MpaDto.values().size()) {
            throw new UserNotFoundException("Invalid MPA rating ID");
        }

        if (film.getGenres() != null) {
            for (GenreDto genre : film.getGenres()) {
                if (genre.getId() < 1 || genre.getId() > GenreDto.values().size()) {
                    throw new UserNotFoundException("Invalid genre ID: " + genre.getId());
                }
            }
        }
    }
}