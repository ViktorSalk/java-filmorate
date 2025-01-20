package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film addFilm(Film postFilm);

    Film updateFilm(Film putFilm);

    Film getFilm(Long id);

    Map<Long, Film> getAllFilmsMap();

    void setAllFilmsMap(Map<Long, Film> allFilmsMap);
}