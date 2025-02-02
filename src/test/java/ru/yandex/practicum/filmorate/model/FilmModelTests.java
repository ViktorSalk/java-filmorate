package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilmModelTests {
    @Test // Тест на получение жанров фильма
    void testFilmGenres() {
        Film film = new Film();
        film.getGenres().add(Genre.COMEDY);
        film.getGenres().add(Genre.DRAMA);
        film.getGenres().add(Genre.CARTOON);
        film.getGenres().add(Genre.THRILLER);
        film.getGenres().add(Genre.DOCUMENTARY);
        film.getGenres().add(Genre.ACTION);

        assertEquals(6, film.getGenres().size());
        assertTrue(film.getGenres().contains(Genre.COMEDY));
        assertTrue(film.getGenres().contains(Genre.DRAMA));
        assertTrue(film.getGenres().contains(Genre.CARTOON));
        assertTrue(film.getGenres().contains(Genre.THRILLER));
        assertTrue(film.getGenres().contains(Genre.DOCUMENTARY));
        assertTrue(film.getGenres().contains(Genre.ACTION));
    }

    @Test // Тест на получение рейтинга фильма
    void testFilmMpa() {
        Film film = new Film();
        film.setMpa(Mpa.PG13);

        assertEquals(Mpa.PG13, film.getMpa());
        assertEquals("PG-13", film.getMpa().getName());
    }
}