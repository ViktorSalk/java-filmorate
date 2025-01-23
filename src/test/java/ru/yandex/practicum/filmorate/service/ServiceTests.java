package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            user1.setEmail("user1@mail.com");
            user1.setLogin("user1");
            user1.setName("User 1");
            user1.setBirthday(LocalDate.of(1990, 1, 1));

            User user2 = new User();
            user2.setEmail("user2@mail.com");
            user2.setLogin("user2");
            user2.setName("User 2");
            user2.setBirthday(LocalDate.of(1990, 1, 2));

            user1 = userService.addUser(user1);
            user2 = userService.addUser(user2);

            userService.addFriend(user1.getId(), user2.getId());
            assertTrue(userService.allIdFriends(user1.getId()).contains(user2));

            userService.deleteFriend(user1.getId(), user2.getId());
            assertFalse(userService.allIdFriends(user1.getId()).contains(user2));
        }

        @Test
            // Тесты для работы с общими друзьями
        void testCommonFriends() {
            User user1 = new User();
            user1.setEmail("user1@mail.com");
            user1.setLogin("user1");
            user1.setName("User 1");
            user1.setBirthday(LocalDate.of(1990, 1, 1));

            User user2 = new User();
            user2.setEmail("user2@mail.com");
            user2.setLogin("user2");
            user2.setName("User 2");
            user2.setBirthday(LocalDate.of(1990, 1, 2));

            User user3 = new User();
            user3.setEmail("user3@mail.com");
            user3.setLogin("user3");
            user3.setName("User 3");
            user3.setBirthday(LocalDate.of(1990, 1, 3));

            user1 = userService.addUser(user1);
            user2 = userService.addUser(user2);
            user3 = userService.addUser(user3);

            userService.addFriend(user1.getId(), user3.getId());
            userService.addFriend(user2.getId(), user3.getId());

            Collection<User> commonFriends = userService.generalFriends(user1.getId(), user2.getId());
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

            user = new User();
            user.setEmail("user@mail.com");
            user.setLogin("user");
            user.setName("User");
            user.setBirthday(LocalDate.of(1990, 1, 1));

            film = filmService.addFilm(film);
            user = userService.addUser(user);
        }

        @Test
            // Тесты для работы с лайками
        void testLikeOperations() {
            filmService.addLike(user.getId(), film.getId());
            assertTrue(film.getIdUserLikes().contains(user.getId()));

            filmService.deleteLike(user.getId(), film.getId());
            assertFalse(film.getIdUserLikes().contains(user.getId()));
        }

        @Test
            // Тесты для работы с популярными фильмами
        void testPopularFilms() {
            Film film2 = new Film();
            film2.setName("Film 2");
            film2.setDescription("Description 2");
            film2.setReleaseDate(LocalDate.of(2000, 1, 2));
            film2.setDuration(120);
            film2 = filmService.addFilm(film2);

            filmService.addLike(user.getId(), film.getId());

            List<Film> popularFilms = new ArrayList<>(filmService.getPopularFilms(10));
            assertEquals(film.getId(), popularFilms.get(0).getId());
        }
    }
}


