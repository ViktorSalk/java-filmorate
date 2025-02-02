package ru.yandex.practicum.filmorate.model;

public enum Mpa {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String name;

    Mpa(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}