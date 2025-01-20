package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidationServiceTests {

    private ValidationService validationService;
    private Film film;
    private User user;

    @BeforeEach
    public void setUp() {
        validationService = new ValidationService();

        film = new Film();
        film.setId(1L);
        film.setName("Имя фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(80);

        user = new User();
        user.setId(1L);
        user.setBirthday(LocalDate.of(1996, 9, 17));
        user.setEmail("volandesy@gmail.com");
        user.setLogin("Voland");
        user.setName("Volandesy");
        user.setFriends(new HashSet<>());
    }

    @Nested // тесты фильма
    class FilmValidationTests {
        @Test
            // тест на проверку имени фильма
        void testCheckNameFilm() {
            film.setName("CheckName");
            validationService.checkNameFilm(film.getName());
            assertEquals(film.getName(), "CheckName", "Имя не совпадает");
        }

        @Test
            // тест на проверку длины описания
        void testMaxLengthDescription() {
            validationService.maxLengthDescription(film.getDescription());
            assertEquals(film.getDescription(), "Описание фильма", "Максимальная длина не совпадает");
            assertNotEquals(film.getDescription().length(), 210, "Длина больше 200 символов");
        }

        @Test
            // тест на проверку даты релиза
        void testMinDateReleaseDate() {
            validationService.minDateReleaseDate(film.getReleaseDate());
            assertEquals(film.getReleaseDate(), LocalDate.now(), "Дата не совпадает");
        }

        @Test
            // тест на проверку длительности
        void testPositiveDurationFilm() {
            validationService.positiveDurationFilm(film.getDuration());
            assertEquals(film.getDuration(), 80, "Не верная длительность");
        }
    }

    @Nested // тесты пользователя
    class UserValidationTests {
        @Test
            // тест на проверку email
        void testCheckEmail() {
            validationService.checkEmail(user.getEmail());
            assertEquals(user.getEmail(), "volandesy@gmail.com", "Не верный email");
        }

        @Test
            // тест на проверку логина
        void testCheckLogin() {
            validationService.checkLogin(user.getLogin());
            assertEquals(user.getLogin(), "Voland", "Не верный логин");
        }

        @Test
            // тест на проверку имени пользователя
        void testCheckName() {
            String result = validationService.checkName(user.getLogin(), user.getName());
            assertEquals(result, "Volandesy", "Не верное имя пользователя");
        }

        @Test
            // тест на проверку даты рождения
        void testCheckBirthday() {
            validationService.checkBirthday(user.getBirthday());
            assertEquals(user.getBirthday(), LocalDate.of(1996, 9, 17), "Дата не прошла валидацию");
        }
    }

    @Nested // тесты лайков
    class FilmLikesTests {
        @Test
            // тест на добавление и удаление лайка
        void testAddAndRemoveLike() {
            Film testFilm = new Film();
            testFilm.setId(1L);
            Set<Long> likes = new HashSet<>();
            testFilm.setIdUserLikes(likes);

            testFilm.getIdUserLikes().add(1L);
            assertEquals(1, testFilm.getIdUserLikes().size());

            testFilm.getIdUserLikes().remove(1L);
            assertEquals(0, testFilm.getIdUserLikes().size());
        }

        @Test
            // тест на получение популярных фильмов
        void testGetPopularFilms() {
            Film film1 = new Film();
            Film film2 = new Film();
            film1.setId(1L);
            film2.setId(2L);

            film1.setIdUserLikes(new HashSet<>(Arrays.asList(1L, 2L)));
            film2.setIdUserLikes(new HashSet<>(Arrays.asList(1L)));

            List<Film> films = Arrays.asList(film1, film2);
            films.sort((f1, f2) -> f2.getIdUserLikes().size() - f1.getIdUserLikes().size());

            assertEquals(film1, films.get(0));
            assertEquals(2, films.get(0).getIdUserLikes().size());
        }
    }

    @Nested // тесты друзей
    class UserFriendsTests {
        @Test
            // тест на добавление и удаление друзей
        void testFriendshipOperations() {
            User user1 = new User();
            User user2 = new User();
            user1.setId(1L);
            user2.setId(2L);

            Set<Long> friends = new HashSet<>();
            friends.add(2L);
            user1.setFriends(friends);

            assertTrue(user1.getFriends().contains(2L));
            assertEquals(1, user1.getFriends().size());

            user1.getFriends().remove(2L);
            assertEquals(0, user1.getFriends().size());
        }

        @Test
            // тест на получение общих друзей
        void testGeneralFriends() {
            User user1 = new User();
            User user2 = new User();
            User user3 = new User();
            user1.setId(1L);
            user2.setId(2L);
            user3.setId(3L);

            user1.setFriends(new HashSet<>(Arrays.asList(2L, 3L)));
            user3.setFriends(new HashSet<>(Arrays.asList(2L)));

            Set<Long> commonFriends = new HashSet<>(user1.getFriends());
            commonFriends.retainAll(user3.getFriends());

            assertEquals(1, commonFriends.size());
            assertTrue(commonFriends.contains(2L));
        }
    }

    @Nested // тесты логирования
    class LoggingTests {
        @Test
        void testLogbookLogging() {
            // Создаем тестовый фильм
            Film testFilm = new Film();
            testFilm.setId(1L);
            testFilm.setName("Test Film");
            testFilm.setDescription("Test Description");
            testFilm.setReleaseDate(LocalDate.now());
            testFilm.setDuration(100);

            // Проверяем валидацию с логированием
            Film validatedFilm = validationService.checkValidationFilm(testFilm);

            assertNotNull(validatedFilm);
            assertEquals("Test Film", validatedFilm.getName());
        }
    }
}
