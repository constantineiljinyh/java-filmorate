package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(HttpStatus badRequest, String message) {
    }
}