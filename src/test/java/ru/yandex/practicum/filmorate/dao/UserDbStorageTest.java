package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase // Тесты будут выполняться в БД H2
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(UserDbStorage.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test // Тест на создание и получение пользователя
    void shouldCreateAndGetUser() {
        User user = createTestUser("create");
        User savedUser = userStorage.create(user);

        assertThat(savedUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", savedUser.getId());
    }

    @Test // Тест на обновление пользователя
    void shouldUpdateUser() {
        User user = userStorage.create(createTestUser("update"));
        String newName = "Updated Name";
        user.setName(newName);

        User updatedUser = userStorage.update(user);
        assertThat(updatedUser).hasFieldOrPropertyWithValue("name", newName);
    }

    @Test // Тест на добавление и удаление друзей
    void shouldAddAndRemoveFriend() {
        User user1 = userStorage.create(createTestUser("friend1"));
        User user2 = userStorage.create(createTestUser("friend2"));

        userStorage.addFriend(user1.getId(), user2.getId());
        assertThat(userStorage.getFriends(user1.getId())).contains(user2);

        userStorage.deleteFriend(user1.getId(), user2.getId());
        assertThat(userStorage.getFriends(user1.getId())).doesNotContain(user2);
    }

    @Test // Тест на получение общих друзей
    void shouldGetCommonFriends() {
        User user1 = userStorage.create(createTestUser("common1"));
        User user2 = userStorage.create(createTestUser("common2"));
        User user3 = userStorage.create(createTestUser("common3"));

        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        assertThat(userStorage.getCommonFriends(user1.getId(), user2.getId())).contains(user3);
    }

    private User createTestUser(String uniqueId) {
        User user = new User();
        user.setEmail(uniqueId + "_" + System.nanoTime() + "@test.com");
        user.setLogin("testuser_" + uniqueId);
        user.setName("Test User " + uniqueId);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}