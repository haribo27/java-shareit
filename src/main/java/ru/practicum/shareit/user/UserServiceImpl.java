package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotUniqueDataException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequestDto request) {
        log.info("Creating new user {}", request);
        if (!isEmailUnique(request.getEmail())) {
            throw new NotUniqueDataException("Email must be unique");
        }
        User user = UserMapper.mapToUser(request);
        user = userRepository.createUser(user);
        log.info("Created user {}", user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Deleting user with id: {}", userId);
        userRepository.findUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.deleteUser(userId);
        log.info("User with id: {} deleted", userId);
    }

    @Override
    public UserDto updateUser(UpdateUserRequestDto request, long userId) {
        log.info("Updating user {}", request);
        if (!isEmailUnique(request.getEmail())) {
            throw new NotUniqueDataException("Email must be unique");
        }
        User updatedUser = userRepository.findUserById(userId)
                .map(user1 -> UserMapper.updateUserFields(user1, request))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        updatedUser = userRepository.updateUser(updatedUser, userId);
        log.info("User {} updated", updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto findUserById(long userId) {
        log.info("Getting user with id: {}", userId);
        return userRepository.findUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public boolean isEmailUnique(String email) {
        List<String> emails = userRepository.getAllEmails();
        if (emails.isEmpty()) {
            return true;
        }
        return !emails.contains(email);
    }
}
