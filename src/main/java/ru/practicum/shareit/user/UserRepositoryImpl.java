package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long currentUserId;

    @Override
    public User createUser(User user) {
        user.setId(currentUserId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public User updateUser(User user, long userId) {
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<String> getAllEmails() {
        return new ArrayList<>(users.values().stream().map(User::getEmail).toList());
    }
}
