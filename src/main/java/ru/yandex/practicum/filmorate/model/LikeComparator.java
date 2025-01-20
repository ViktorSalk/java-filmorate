package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class LikeComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        int size1;
        int size2;
        if (film1.getIdUserLikes() != null) {
            size1 = film1.getIdUserLikes().size();
        } else {
            size1 = 0;
        }
        if (film2.getIdUserLikes() != null) {
            size2 = film2.getIdUserLikes().size();
        } else {
            size2 = 0;
        }

        int comparison = Integer.compare(size2, size1);

        if (comparison == 0) {
            return Long.compare(film1.getId(), film2.getId());
        }

        return comparison;
    }
}