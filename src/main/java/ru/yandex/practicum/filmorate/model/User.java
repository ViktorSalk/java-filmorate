package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    public void setLogin(String login) {
        this.login = login;
        // Устанавливаем name, если он не задан
        if (this.name == null || this.name.isBlank()) {
            this.name = login; // Или любое другое значение по умолчанию
        }
    }

    private Set<User> friends = new HashSet<>();

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }
}