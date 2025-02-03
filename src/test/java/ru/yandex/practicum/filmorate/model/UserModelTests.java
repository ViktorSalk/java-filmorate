package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTests {

    @Test
    void testFriendshipWorkflow() {
        User user1 = new User();
        User user2 = new User();
        user1.setId(1L);
        user2.setId(2L);

        // Первоначальный запрос в друзья
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());

        // Проверьте статус неподтвержденный после запроса
        assertEquals(FriendshipStatus.UNCONFIRMED, user1.getFriendshipStatus(user2.getId()));
        assertEquals(FriendshipStatus.UNCONFIRMED, user2.getFriendshipStatus(user1.getId()));

        // Пользователь2 подтверждает дружбу
        user1.getFriendshipStatuses().put(user2.getId(), FriendshipStatus.CONFIRMED);
        user2.getFriendshipStatuses().put(user1.getId(), FriendshipStatus.CONFIRMED);

        // Проверьте подтвержденный статус
        assertEquals(FriendshipStatus.CONFIRMED, user1.getFriendshipStatus(user2.getId()));
        assertEquals(FriendshipStatus.CONFIRMED, user2.getFriendshipStatus(user1.getId()));

        // Проверьте несуществующий статус дружбы
        Long nonExistentUserId = 999L;
        assertEquals(FriendshipStatus.UNCONFIRMED, user1.getFriendshipStatus(nonExistentUserId));
    }
}