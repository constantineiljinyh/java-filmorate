package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.ClassificationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/mpa")
public class RatingMpaController {

    private final ClassificationService<RatingMPA> ratingMpaService;

    @GetMapping
    public List<RatingMPA> getAllRatingMpa() {
        log.debug("Поступил запрос на просмотр всех рейтингов.");
        return ratingMpaService.getAll();
    }

    @GetMapping("/{id}")
    public RatingMPA getRantingMpaById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр рейтинга с id {}.", id);
        return ratingMpaService.getById(id);
    }
}
