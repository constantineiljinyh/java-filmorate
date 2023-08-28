package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositoryTest {
    @Autowired
    private UserRepository userDbStorage;

    @Test
    public void testAddUser() {
        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        User addedUser = userDbStorage.add(userToAdd);

        assertNotNull(addedUser);
        assertNotNull(addedUser.getId());
        assertEquals(userToAdd.getName(), addedUser.getName());
        assertEquals(userToAdd.getEmail(), addedUser.getEmail());
        assertEquals(userToAdd.getLogin(), addedUser.getLogin());
        assertEquals(userToAdd.getBirthday(), addedUser.getBirthday());
    }

    @Test
    public void testGetAll() {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setLogin("johndoe");
        user1.setBirthday(LocalDate.of(1990, 5, 15));

        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        user2.setLogin("janesmith");
        user2.setBirthday(LocalDate.of(1985, 8, 22));

        userDbStorage.add(user1);
        userDbStorage.add(user2);

        List<User> userList = userDbStorage.getAll();

        assertEquals(2, userList.size());
    }

    @Test
    public void testUpdateUser() {
        User userToAdd = new User();
        userToAdd.setName("Original Name");
        userToAdd.setEmail("original@example.com");
        userToAdd.setLogin("originaluser");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));
        userDbStorage.add(userToAdd);

        userToAdd.setName("Updated Name");
        userToAdd.setEmail("updated@example.com");
        userToAdd.setLogin("updateduser");
        userToAdd.setBirthday(LocalDate.of(1995, 8, 20));

        User updatedUser = userDbStorage.update(userToAdd);

        assertNotNull(updatedUser);
        assertEquals(userToAdd.getId(), updatedUser.getId());
        assertEquals(userToAdd.getName(), updatedUser.getName());
        assertEquals(userToAdd.getEmail(), updatedUser.getEmail());
        assertEquals(userToAdd.getLogin(), updatedUser.getLogin());
        assertEquals(userToAdd.getBirthday(), updatedUser.getBirthday());

        User retrievedUser = userDbStorage.getById(updatedUser.getId());
        assertNotNull(retrievedUser);
        assertEquals(userToAdd.getId(), retrievedUser.getId());
        assertEquals(userToAdd.getName(), retrievedUser.getName());
        assertEquals(userToAdd.getEmail(), retrievedUser.getEmail());
        assertEquals(userToAdd.getLogin(), retrievedUser.getLogin());
        assertEquals(userToAdd.getBirthday(), retrievedUser.getBirthday());
    }

    @Test
    public void testGetUserById() {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setLogin("johndoe");
        user1.setBirthday(LocalDate.of(1990, 5, 15));

        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        user2.setLogin("janesmith");
        user2.setBirthday(LocalDate.of(1985, 8, 22));

        userDbStorage.add(user1);
        userDbStorage.add(user2);

        User retrievedUser = userDbStorage.getById(user1.getId());

        assertNotNull(retrievedUser);
        assertEquals(user1.getId(), retrievedUser.getId());
        assertEquals(user1.getName(), retrievedUser.getName());
        assertEquals(user1.getEmail(), retrievedUser.getEmail());
        assertEquals(user1.getLogin(), retrievedUser.getLogin());
        assertEquals(user1.getBirthday(), retrievedUser.getBirthday());
    }

    @Test
    public void testRemoveUser() {
        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        User addedUser = userDbStorage.add(userToAdd);

        User removedUser = userDbStorage.remove(addedUser.getId());

        assertNotNull(removedUser);
        assertEquals(addedUser.getId(), removedUser.getId());
        assertEquals(addedUser.getName(), removedUser.getName());
        assertEquals(addedUser.getEmail(), removedUser.getEmail());
        assertEquals(addedUser.getLogin(), removedUser.getLogin());
        assertEquals(addedUser.getBirthday(), removedUser.getBirthday());

        assertThrows(NotFoundException.class, () -> userDbStorage.getById(addedUser.getId()));
    }

    @Test
    public void testIsExistReturnsTrue() {
        User userToAdd = new User();
        userToAdd.setName("John Doe");
        userToAdd.setEmail("john@example.com");
        userToAdd.setLogin("johndoe");
        userToAdd.setBirthday(LocalDate.of(1990, 5, 15));

        User addedUser = userDbStorage.add(userToAdd);
        int existingUserId = addedUser.getId();

        assertTrue(userDbStorage.isExist(existingUserId));
    }

    @Test
    public void testIsExistReturnsFalse() {
        int nonExistentUserId = -1;

        assertFalse(userDbStorage.isExist(nonExistentUserId));
    }
}