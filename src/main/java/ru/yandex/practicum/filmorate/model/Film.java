package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDateValidation;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    @ReleaseDateValidation
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private MpaDto mpa;
    private Set<GenreDto> genres = new TreeSet<>(Comparator.comparing(GenreDto::getId));
    private Set<Long> likes = new HashSet<>();
}