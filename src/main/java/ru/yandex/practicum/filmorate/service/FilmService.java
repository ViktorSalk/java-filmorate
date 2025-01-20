package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        Map<Long, Film> allFilmsMap = filmStorage.getAllFilmsMap();
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        Set<Long> filmLikes = film.getIdUserLikes();
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        if (filmLikes.contains(userId)) {
            log.info("Пользователь с id: " + userId + "уже ставил лайк");
            return;
        }
        filmLikes.add(userId);
        film.setIdUserLikes(filmLikes);
        allFilmsMap.put(film.getId(), film);
        filmStorage.setAllFilmsMap(allFilmsMap);
        log.info("Пользователь c id: " + userId + " поставил лайк");
    }

    public void deleteLike(Long userId, Long id) {
        Map<Long, Film> allFilmsMap = filmStorage.getAllFilmsMap();
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);
        Set<Long> filmLikes = film.getIdUserLikes();
        if (filmLikes == null) {
            filmLikes = new HashSet<>();
        }
        if (!filmLikes.contains(userId)) {
            log.info("Пользователь с id: " + userId + " нет в списке тех кто ставил лайк");
            return;
        }
        filmLikes.remove(userId);
        film.setIdUserLikes(filmLikes);
        allFilmsMap.put(film.getId(), film);
        filmStorage.setAllFilmsMap(allFilmsMap);
        log.info("Пользователь c id: " + userId + " удалил лайк");
    }

    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> filmSet = filmStorage.getAllFilms();
        TreeSet<Film> filmTreeSet = new TreeSet<>(likeComparator);
        filmTreeSet.addAll(filmSet);
        List<Film> filmList = new ArrayList<>(filmTreeSet);

        if (count > filmList.size()) {
            count = filmList.size();
        }
        return filmList.subList(0, count);
    }

    public Map<Long, Film> getAllMapFilms() {
        return filmStorage.getAllFilmsMap();
    }
}