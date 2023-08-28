package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.ClassificationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final ClassificationService<Genre> genreService;

    @GetMapping
    public List<Genre> getAll() {
        log.debug("Поступил запрос на просмотр всех жанров.");
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр жанра с id {}.", id);
        return genreService.getById(id);
    }
}
