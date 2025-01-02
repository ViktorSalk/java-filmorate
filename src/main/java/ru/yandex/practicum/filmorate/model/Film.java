package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
public class Film {

    private Long id;

    @NotNull(message = "Название фильма не может быть пустым")
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull(message = "Описание фильма не может быть пустым")
    @NotBlank(message = "Описание фильма не может быть пустым")
    private String description;

    @NotNull(message = "Дата выхода не может быть пустой")
    private LocalDate releaseDate;

    @NotNull(message = "Длительность не может быть пустой")
    private Integer duration;
}