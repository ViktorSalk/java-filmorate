package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTests {

    @Autowired
    private Validator validator;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("name");
        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().minusDays(1));
    }

    @Nested
    class LoginTests { // Валидация логина
        @Test
        void shouldPassValidationWithCorrectLogin() {
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithNullLogin() {
            user.setLogin(null);
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithEmptyLogin() {
            user.setLogin("");
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithSpacesInLogin() {
            user.setLogin("user login");
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }
    }

    @Nested
    class EmailTests { // Валидация почты
        @Test
        void shouldPassValidationWithCorrectEmail() {
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithNullEmail() {
            user.setEmail(null);
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithEmptyEmail() {
            user.setEmail("");
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithInvalidEmail() {
            user.setEmail("useremail.ru");
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }
    }

    @Nested
    class BirthdayTests { // Валидация даты рождения
        @Test
        void shouldPassValidationWithCorrectBirthday() {
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }

        @Test
        void shouldPassValidationWithFutureBirthday() {
            user.setBirthday(LocalDate.now().plusDays(1));
            Set<ConstraintViolation<User>> violation = validator.validate(user);
            assertTrue(violation.isEmpty());
        }
    }
}