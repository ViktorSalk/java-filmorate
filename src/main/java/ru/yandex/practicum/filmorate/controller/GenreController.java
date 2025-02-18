package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    @GetMapping
    public List<GenreDto> getAllGenres() {
        return Arrays.stream(Genre.values())
                .map(genre -> {
                    GenreDto dto = new GenreDto();
                    dto.setId(genre.ordinal() + 1);
                    dto.setName(genre.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable int id) {
        if (id < 1 || id > Genre.values().length) {
            throw new UserNotFoundException("Genre not found");
        }
        Genre genre = Genre.values()[id - 1];
        GenreDto dto = new GenreDto();
        dto.setId(id);
        dto.setName(genre.getName());
        return dto;
    }
}