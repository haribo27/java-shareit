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
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(NewUserRequestDto request) {
        log.info("Creating new user {}", request);
        if (!isEmailUnique(request.getEmail())) {
            throw new NotUniqueDataException("Email must be unique");
        }
        User user = userMapper.toUser(request);
        user = userRepository.createUser(user);
        log.info("Created user {}", user);
        return userMapper.toUserDto(user);
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
        User updatedUser = userRepository.findUserById(userId)
                .map(user -> {
                    boolean isEmailChanged = request.getEmail() != null && !request.getEmail().equals(user.getEmail());
                    if (isEmailChanged && !isEmailUnique(request.getEmail())) {
                        throw new NotUniqueDataException("Email must be unique");
                    }
                    return userMapper.updateUserRequest(request);
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        updatedUser = userRepository.updateUser(updatedUser, userId);
        log.info("User {} updated", updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userRepository.getAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto findUserById(long userId) {
        log.info("Getting user with id: {}", userId);
        return userRepository.findUserById(userId)
                .map(userMapper::toUserDto)
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
