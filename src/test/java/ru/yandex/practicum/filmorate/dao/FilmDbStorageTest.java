package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase // Тестирование с использованием H2
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test // Тест на создание и получение фильма
    void shouldCreateAndGetFilm() {
        Film film = createTestFilm();
        Film savedFilm = filmStorage.create(film);

        assertThat(savedFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", savedFilm.getId());
    }

    @Test // Тест на обновление фильма
    void shouldUpdateFilm() {
        Film savedFilm = filmStorage.create(createTestFilm());
        String newName = "Updated Name";
        savedFilm.setName(newName);

        Film updatedFilm = filmStorage.update(savedFilm);
        assertThat(updatedFilm).hasFieldOrPropertyWithValue("name", newName);
    }

    @Test // Тест на добавление и удаление лайка
    void shouldAddAndRemoveLike() {
        Film film = filmStorage.create(createTestFilm());
        User user = userStorage.create(createTestUser());

        filmStorage.addLike(film.getId(), user.getId());
        assertThat(filmStorage.get(film.getId()).getLikes()).contains(user.getId());

        filmStorage.deleteLike(film.getId(), user.getId());
        assertThat(filmStorage.get(film.getId()).getLikes()).doesNotContain(user.getId());
    }

    @Test // Тест на получение популярных фильмов
    void shouldGetPopularFilms() {
        Film film1 = filmStorage.create(createTestFilm());
        filmStorage.create(createTestFilm());
        User user = userStorage.create(createTestUser());

        filmStorage.addLike(film1.getId(), user.getId());

        List<Film> popularFilms = filmStorage.getPopular(10);
        assertThat(popularFilms)
                .isNotEmpty()
                .first()
                .hasFieldOrPropertyWithValue("id", film1.getId());
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        MpaDto mpa = new MpaDto();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        return film;
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test" + System.currentTimeMillis() + "@test.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}