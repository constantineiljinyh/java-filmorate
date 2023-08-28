package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.repository.rating.RatingMPARepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RatingMPAServiceImpl implements ClassificationService<RatingMPA> {

    private final RatingMPARepository ratingMpa;

    @Override
    public List<RatingMPA> getAll() {
        log.debug("Обрабатываем запрос на получение всех рейтингов МРА.");
        return ratingMpa.getAll();
    }

    @Override
    public RatingMPA getById(Integer id) {
        if (!ratingMpa.isExist(id)) {
            log.debug("Обрабатываем запрос на получение не существующего Рейтинга MPA {}", id);
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Такого рейтинга MPA не существует");
        }
        return ratingMpa.getById(id);
    }
}
