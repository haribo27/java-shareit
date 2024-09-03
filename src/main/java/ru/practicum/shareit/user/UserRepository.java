package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    void deleteUser(long userId);

    User updateUser(User user, long userId);

    List<User> getAllUsers();

    Optional<User> findUserById(long userId);

    List<String> getAllEmails();
}
