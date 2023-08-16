package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface ClassificationStorage<I> {

    List<I> getAll();

    I getById(Integer genreId);

    boolean isExist(int id);
}
