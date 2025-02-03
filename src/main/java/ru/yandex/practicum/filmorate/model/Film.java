package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    @ReleaseDateValidation(message = "Release date cannot be earlier than December 28, 1895")
    private LocalDate releaseDate;

    @PositiveOrZero(message = "Длительность фильма должна быть больше или равна нулю")
    private Integer duration;
    private Set<Long> idUserLikes = new HashSet<>();

    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
}