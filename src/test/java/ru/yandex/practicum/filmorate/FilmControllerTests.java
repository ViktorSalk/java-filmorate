package ru.yandex.practicum.filmorate;

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

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Validator validator;

    private Film film;
    private final String correctDescription = String.valueOf(200);
    private final String incorrectDescription = String.valueOf(201);
    private final LocalDate correctDate = LocalDate.of(1985, 12, 28);

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("film");
        film.setDescription(correctDescription);
        film.setReleaseDate(LocalDate.of(1985, 12, 28));
        film.setDuration(60);
    }


    @Nested
    class NameTests { // Валидация названия
        @Test
        void shouldPassValidationWithCorrectName() {
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithNullName() {
            film.setName(null);
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithEmptyName() {
            film.setName("");
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }
    }

    @Nested
    class DescriptionTests { // Валидация описания
        @Test
        void shouldPassValidationWithCorrectDescription() {
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithIncorrectDescription() {
            film.setDescription(incorrectDescription);
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }
    }

    @Nested
    class DateTests { // Валидация даты
        @Test
        void shouldPassValidationWithCorrectDate() {
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithDateBeforeCorrectDate() {
            film.setReleaseDate(correctDate.minusDays(1));
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }
    }

    @Nested
    class DurationTests { // Валидация длительности
        @Test
        void shouldPassValidationWithCorrectDuration() {
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithNegativeDuration() {
            film.setDuration(-1);
            Set<ConstraintViolation<Film>> violation = validator.validate(film);
            assertTrue(violation.isEmpty());
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

            assertEquals(HttpStatus.OK, response.getStatusCode());
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