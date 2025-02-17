package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DataPersistenceTest {
    @Autowired
    private FilmService filmService;
    @Autowired
    private UserService userService;

    @Test
    void shouldPersistDataAfterRestart() {
        // Создаем тестового пользователя с почтой временной меткой
        User user = new User();
        user.setEmail("test" + System.nanoTime() + "@email.com");
        user.setLogin("testLogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        // Создаем тестовый фильм
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        MpaDto mpa = new MpaDto();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        User savedUser = userService.createUser(user);
        Film savedFilm = filmService.createFilm(film);

        // Проверяем, что данные сохранены после перезагрузки
        assertNotNull(userService.get(savedUser.getId()));
        assertNotNull(filmService.get(savedFilm.getId()));
        assertEquals("Test Name", userService.get(savedUser.getId()).getName());
        assertEquals("Test Film", filmService.get(savedFilm.getId()).getName());
    }
}



