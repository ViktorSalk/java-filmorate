package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MpaDto {
    private int id;
    private String name;

    public static List<MpaDto> values() {
        return Arrays.asList(
                new MpaDto(1, "G"),
                new MpaDto(2, "PG"),
                new MpaDto(3, "PG-13"),
                new MpaDto(4, "R"),
                new MpaDto(5, "NC-17")
        );
    }
}