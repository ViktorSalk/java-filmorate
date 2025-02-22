package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreDto {
    private int id;
    private String name;

    public static List<GenreDto> values() {
        return Arrays.asList(
                new GenreDto(1, "Комедия"),
                new GenreDto(2, "Драма"),
                new GenreDto(3, "Мультфильм"),
                new GenreDto(4, "Триллер"),
                new GenreDto(5, "Документальный"),
                new GenreDto(6, "Боевик")
        );
    }
}