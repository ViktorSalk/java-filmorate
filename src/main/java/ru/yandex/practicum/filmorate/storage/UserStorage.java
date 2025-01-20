package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public interface UserStorage {
    Collection<User> getAllUsers();

    User addUser(User postUser);

    User updateUser(User putUser);

    Map<Long, User> getCollectionAllUsers();

    void setCollectionAllUsers(Map<Long, User> allUsers1);

    void setFriendsMap(Map<Long, Set<User>> friendsMap);

    Map<Long, Set<User>> getFriendsMap();

    User getUser(long id);

    Set<User> getUserFriends(long id);

    void updateUsersFriends(long id, Set<User> userSetFriends);
}