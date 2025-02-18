package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ServiceTests {
    @Autowired
    private UserService userService;
    @Autowired
    private FilmService filmService;

    @Nested
    class FriendshipTests {
        @Test
            // Тесты для работы с друзьями
        void testFriendshipOperations() {
            User user1 = new User();
            user1.setEmail("friendship1" + System.currentTimeMillis() + "@test.com");
            user1.setLogin("user1");
            user1.setName("User 1");
            user1.setBirthday(LocalDate.of(1990, 1, 1));

            User user2 = new User();
            user2.setEmail("friendship2" + System.currentTimeMillis() + "@test.com");
            user2.setLogin("user2");
            user2.setName("User 2");
            user2.setBirthday(LocalDate.of(1990, 1, 2));

            user1 = userService.createUser(user1);
            user2 = userService.createUser(user2);

            userService.addFriend(user1.getId(), user2.getId());
            assertTrue(userService.getFriends(user1.getId()).contains(user2));

            userService.deleteFriend(user1.getId(), user2.getId());
            assertFalse(userService.getFriends(user1.getId()).contains(user2));
        }

        @Test
            // Тесты для работы с общими друзьями
        void testCommonFriends() {
            User user1 = new User();
            user1.setEmail("common1" + System.currentTimeMillis() + "@test.com");
            user1.setLogin("user1");
            user1.setName("User 1");
            user1.setBirthday(LocalDate.of(1990, 1, 1));

            User user2 = new User();
            user2.setEmail("common2" + System.currentTimeMillis() + "@test.com");
            user2.setLogin("user2");
            user2.setName("User 2");
            user2.setBirthday(LocalDate.of(1990, 1, 2));

            User user3 = new User();
            user3.setEmail("common3" + System.currentTimeMillis() + "@test.com");
            user3.setLogin("user3");
            user3.setName("User 3");
            user3.setBirthday(LocalDate.of(1990, 1, 3));

            user1 = userService.createUser(user1);
            user2 = userService.createUser(user2);
            user3 = userService.createUser(user3);

            userService.addFriend(user1.getId(), user3.getId());
            userService.addFriend(user2.getId(), user3.getId());

            Collection<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());
            assertTrue(commonFriends.contains(user3));
        }
    }

    @Nested
    class FilmLikesTests {
        private Film film;
        private User user;

        @BeforeEach
        void setUp() {
            film = new Film();
            film.setName("Test Film");
            film.setDescription("Test Description");
            film.setReleaseDate(LocalDate.of(2000, 1, 1));
            film.setDuration(120);

            MpaDto mpa = new MpaDto();
            mpa.setId(1);
            mpa.setName("G");
            film.setMpa(mpa);

            user = new User();
            user.setEmail("likes" + System.currentTimeMillis() + "@test.com");
            user.setLogin("user");
            user.setName("User");
            user.setBirthday(LocalDate.of(1990, 1, 1));

            film = filmService.createFilm(film);
            user = userService.createUser(user);
        }

        @Test
            // Тесты для работы с лайками
        void testLikeOperations() {
            filmService.addLike(film.getId(), user.getId());
            Film updatedFilm = filmService.get(film.getId());
            assertTrue(updatedFilm.getLikes().contains(user.getId()));

            filmService.deleteLike(film.getId(), user.getId());
            updatedFilm = filmService.get(film.getId());
            assertFalse(updatedFilm.getLikes().contains(user.getId()));
        }

        @Test
            // Тесты для работы с популярными фильмами
        void testPopularFilms() {
            Film testFilm = new Film();
            testFilm.setName("Test Popular Film");
            testFilm.setDescription("Test Description");
            testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
            testFilm.setDuration(120);

            MpaDto mpa = new MpaDto();
            mpa.setId(1);
            mpa.setName("G");
            testFilm.setMpa(mpa);

            Film savedFilm = filmService.createFilm(testFilm);

            // Добавляем лайк на сохраненный фильм
            filmService.addLike(savedFilm.getId(), user.getId());

            // Получаем популярные фильмы с большим количеством лайков
            List<Film> popularFilms = filmService.getPopularFilms(10);
            assertTrue(popularFilms.get(0).getLikes().size() >= 1);
        }
    }
}