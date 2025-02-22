package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreDto;
import ru.yandex.practicum.filmorate.model.MpaDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        updateFilmGenres(film);
        return get(film.getId());
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";

        if (jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) == 0) {
            throw new UserNotFoundException("Film not found: " + film.getId());
        }

        updateFilmGenres(film);
        return get(film.getId());
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    @Override
    public Film get(Long id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            throw new UserNotFoundException("Film not found: " + id);
        }
        Film film = films.get(0);
        loadGenresForFilm(film);
        loadLikesForFilm(film);
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT f.*, m.name as mpa_name, COUNT(l.user_id) as like_count " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        MpaDto mpa = new MpaDto();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        film.setGenres(new TreeSet<>(Comparator.comparing(GenreDto::getId)));
        film.setLikes(new HashSet<>());
        return film;
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (GenreDto genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        String sql = "SELECT fg.film_id, g.genre_id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id IN (" +
                String.join(",", Collections.nCopies(films.size(), "?")) + ")";

        List<Object> args = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, List<GenreDto>> filmGenresMap = new HashMap<>();
        jdbcTemplate.query(sql, args.toArray(), (rs) -> {
            Long filmId = rs.getLong("film_id");
            GenreDto genre = new GenreDto();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));

            filmGenresMap.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });

        films.forEach(film -> {
            List<GenreDto> genres = filmGenresMap.getOrDefault(film.getId(), new ArrayList<>());
            film.getGenres().addAll(genres);
        });
    }

    private void loadGenresForFilm(Film film) {
        String sql = "SELECT g.* FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<GenreDto> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            GenreDto genre = new GenreDto();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.getGenres().addAll(genres);
    }

    private void loadLikesForFilms(List<Film> films) {
        String sql = "SELECT film_id, user_id FROM likes WHERE film_id IN (" +
                String.join(",", Collections.nCopies(films.size(), "?")) + ")";

        List<Object> args = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, Set<Long>> filmLikesMap = new HashMap<>();
        jdbcTemplate.query(sql, args.toArray(), (rs) -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            filmLikesMap.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        films.forEach(film -> {
            Set<Long> likes = filmLikesMap.getOrDefault(film.getId(), new HashSet<>());
            film.getLikes().addAll(likes);
        });
    }

    private void loadLikesForFilm(Film film) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getLong("user_id"), film.getId());
        film.getLikes().addAll(likes);
    }
}