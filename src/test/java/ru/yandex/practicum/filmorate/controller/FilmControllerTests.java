package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Validator validator;

    private Film film;
    private final String correctDescription = "Test description";
    private final String incorrectDescription = "a".repeat(201);
    private final LocalDate correctDate = LocalDate.of(1895, 12, 28);

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setId(1L); // ID может быть любым
        film.setName("Test Film");
        film.setDescription(correctDescription);
        film.setReleaseDate(LocalDate.of(1985, 12, 28));
        film.setDuration(60);

        MpaDto mpa = new MpaDto();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);
    }

    @Nested
    class NameTests { // Валидация названия
        @Test
        void shouldPassValidationWithCorrectName() {
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertTrue(violations.isEmpty());
        }

        @Test
        void shouldNotPassValidationWithNullName() {
            film.setName(null);
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertFalse(violations.isEmpty()); // Ожидаем, что валидация не пройдет
        }

        @Test
        void shouldNotPassValidationWithEmptyName() {
            film.setName("");
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertFalse(violations.isEmpty()); // Ожидаем, что валидация не пройдет
        }
    }

    @Nested
    class DescriptionTests { // Валидация описания
        @Test
        void shouldPassValidationWithCorrectDescription() {
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertTrue(violations.isEmpty());
        }

        @Test
        void shouldNotPassValidationWithTooLongDescription() {
            film.setDescription(incorrectDescription);
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    class DateTests { // Валидация даты
        @Test
        void shouldPassValidationWithCorrectDate() {
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertTrue(violations.isEmpty());
        }

        @Test
        void shouldNotPassValidationWithTooEarlyDate() {
            film.setReleaseDate(correctDate.minusDays(1));
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    class DurationTests { // Валидация длительности
        @Test
        void shouldPassValidationWithPositiveDuration() {
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertTrue(violations.isEmpty());
        }

        @Test
        void shouldNotPassValidationWithNegativeDuration() {
            film.setDuration(-1);
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertFalse(violations.isEmpty()); // Отрицательная длительность не должна проходить проверку
        }

        @Test
        void shouldNotPassValidationWithZeroDuration() {
            film.setDuration(0);
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    class AddFilmTests { // Добавление фильма
        @Test
        void shouldAddValidFilm() {
            ResponseEntity<Film> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/films",
                    film,
                    Film.class);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertNotNull(response.getBody().getId());
        }

        @Test
        void shouldNotAddInvalidFilm() {
            film.setName("");
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/films",
                    film,
                    String.class);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    class UpdateFilmTests { // Обновление фильма
        @Test
        void shouldUpdateExistingFilm() {
            // Сначала добавляем фильм
            ResponseEntity<Film> addResponse = restTemplate.postForEntity(
                    "http://localhost:" + port + "/films",
                    film,
                    Film.class);

            Film addedFilm = addResponse.getBody();
            assertNotNull(addedFilm);

            // Теперь обновляем фильм
            addedFilm.setName("Updated Film");
            HttpEntity<Film> requestEntity = new HttpEntity<>(addedFilm);

            ResponseEntity<Film> updateResponse = restTemplate.exchange(
                    "http://localhost:" + port + "/films",
                    HttpMethod.PUT,
                    requestEntity,
                    Film.class);

            assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
            assertNotNull(updateResponse.getBody());
            assertEquals("Updated Film", updateResponse.getBody().getName());
        }

        @Test
        void shouldNotUpdateNonExistentFilm() {
            film.setId(9999L); // Несуществующий ID
            HttpEntity<Film> requestEntity = new HttpEntity<>(film);

            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:" + port + "/films",
                    HttpMethod.PUT,
                    requestEntity,
                    String.class);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }
}