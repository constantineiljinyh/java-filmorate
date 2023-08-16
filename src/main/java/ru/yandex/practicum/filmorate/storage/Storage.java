package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    T add(T item);

    List<T> getAll();

    T update(T updatedItem);

    T getById(Integer itemId);

    T remove(Integer filmId);

    boolean isExist(int id);
}