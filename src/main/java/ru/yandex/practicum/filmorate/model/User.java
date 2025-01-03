package ru.yandex.practicum.filmorate.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен соответствовать формату")
    private String email;

    @NotBlank(message = "Login не может быть пустым")
    @Pattern(regexp = "^[^\\s]+$", message = "Login не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public User() {
    }

    @PostConstruct
    private void init() {
        if (name == null || name.isBlank()) {
            name = login;
        }
    }
}