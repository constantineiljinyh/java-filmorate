package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class RatingMPAStorageDbImplTest {

    @Autowired
    private RatingMPAStorageDbImpl ratingMPAStorageDb;

    @Test
    public void testGetAllRatings() {
        List<RatingMPA> ratings = ratingMPAStorageDb.getAll();
        assertNotNull(ratings);
        assertFalse(ratings.isEmpty());
    }

    @Test
    public void testGetRatingById() {
        int ratingId = 1;
        RatingMPA rating = ratingMPAStorageDb.getById(ratingId);
        assertNotNull(rating);
        assertEquals(ratingId, rating.getId());
    }

    @Test
    public void testIsExist() {
        int existingRatingId = 1;
        int nonExistentRatingId = -1;

        assertTrue(ratingMPAStorageDb.isExist(existingRatingId));
        assertFalse(ratingMPAStorageDb.isExist(nonExistentRatingId));
    }
}