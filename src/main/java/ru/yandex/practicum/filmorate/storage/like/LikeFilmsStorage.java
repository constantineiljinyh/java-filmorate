package ru.yandex.practicum.filmorate.storage.like;

public interface LikeFilmsStorage {

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    int getLikeCount(Integer filmId);

    void minusRateForFilm(int filmId);

    void likeForFilm(int filmId, int userId);

    void plusRateForFilm(int filmId);

    boolean isExistLike(int filmId, int userId);

    void dislikeForFilm(int filmId, int userId);
}
