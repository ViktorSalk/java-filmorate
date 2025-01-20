package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата выхода не может быть пустой")
    @ReleaseDateValidation // Кастомная аннотация валидации
    private LocalDate releaseDate;

    @NotNull(message = "Длительность не может быть пустой")
    @PositiveOrZero(message = "Длительность фильма должна быть больше или равна нулю")
    private Integer duration;
    private Set<Long> idUserLikes = new HashSet<>();

    public Set<Long> getIdUserLikes() {
        if (idUserLikes == null) {
            idUserLikes = new HashSet<>();
        }
        return idUserLikes;
    }
}