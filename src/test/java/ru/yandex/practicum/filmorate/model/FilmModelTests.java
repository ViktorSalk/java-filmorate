package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmModelTests {
    @Test // Тест на получение жанров фильма
    void testFilmGenres() {
        Film film = new Film();
        GenreDto comedy = new GenreDto();
        comedy.setId(1);
        comedy.setName("Комедия");

        GenreDto drama = new GenreDto();
        drama.setId(2);
        drama.setName("Драма");

        film.getGenres().add(comedy);
        film.getGenres().add(drama);

        assertEquals(2, film.getGenres().size());
        assertTrue(film.getGenres().contains(comedy));
        assertTrue(film.getGenres().contains(drama));
    }

    @Test // Тест на получение рейтинга фильма
    void testFilmMpa() {
        Film film = new Film();
        MpaDto mpa = new MpaDto();
        mpa.setId(3);
        mpa.setName("PG-13");
        film.setMpa(mpa);

        assertEquals(3, film.getMpa().getId());
        assertEquals("PG-13", film.getMpa().getName());
    }
}