package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    @GetMapping
    public List<MpaDto> getAllMpa() {
        return Arrays.stream(Mpa.values())
                .map(mpa -> {
                    MpaDto dto = new MpaDto();
                    dto.setId(mpa.ordinal() + 1);
                    dto.setName(mpa.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MpaDto getMpaById(@PathVariable int id) {
        if (id < 1 || id > Mpa.values().length) {
            throw new UserNotFoundException("MPA rating not found");
        }
        Mpa mpa = Mpa.values()[id - 1];
        MpaDto dto = new MpaDto();
        dto.setId(id);
        dto.setName(mpa.getName());
        return dto;
    }
}