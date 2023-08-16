package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.ClassificationStorage;

import java.util.List;

@Transactional
@Slf4j
@Service
@AllArgsConstructor
public class GenreServiceImpl implements ClassificationService<Genre> {
    private final ClassificationStorage<Genre> genreStorage;

    public List<Genre> getAll() {
        log.debug("Обрабатываем запрос на просмотр всех жанров фильмов.");
        return genreStorage.getAll();
    }

    public Genre getById(Integer id) {
        log.debug("Обрабатываем запрос на просмотр фильма с id {}.", id);
        if (genreStorage.isExist(id)) {
            return genreStorage.getById(id);
        } else {
            log.debug("Жанра с таким id не существующего {}", id);
            throw new NotFoundException(String.format("Жанра с таким id %d не существует.", id));
        }
    }
}
