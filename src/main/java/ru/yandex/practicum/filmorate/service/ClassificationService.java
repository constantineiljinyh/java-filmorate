package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface ClassificationService<T> {

    List<T> getAll();

    T getById(Integer id);
}
