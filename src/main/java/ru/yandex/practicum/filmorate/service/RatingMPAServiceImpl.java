package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.ClassificationStorage;

import java.util.List;

@Transactional
@Service
@AllArgsConstructor
public class RatingMPAServiceImpl implements ClassificationService<RatingMPA> {

    private final ClassificationStorage<RatingMPA> ratingMpa;

    @Override
    public List<RatingMPA> getAll() {
        return ratingMpa.getAll();
    }

    @Override
    public RatingMPA getById(Integer id) {
        if (!ratingMpa.isExist(id)) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "такого рейтинга MPA не существует");
        }
        return ratingMpa.getById(id);
    }
}
