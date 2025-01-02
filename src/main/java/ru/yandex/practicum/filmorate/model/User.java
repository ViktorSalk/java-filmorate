package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private Long id;

    @NotNull(message = "Email не может быть пустым")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен соответствовать формату")
    private String email;

    @NotNull(message = "Login не может быть пустым")
    @NotBlank(message = "Login не может быть пустым")
    @Pattern(regexp = "^[^\\s]+$", message = "Login не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}