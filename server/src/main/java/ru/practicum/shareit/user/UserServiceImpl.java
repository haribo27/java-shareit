package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExist;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequestDto request) {
        log.info("Creating new user {}", request);
        if (findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExist("Try another email. This already exist");
        }
        User user = userMapper.requestToUser(request);
        user = userRepository.save(user);
        log.info("Created user {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        log.info("Deleting user with id: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.deleteById(userId);
        log.info("User with id: {} deleted", userId);
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdateUserRequestDto request, long userId) {
        log.info("Updating user {}", request);
        User updatedUser = userRepository.findById(userId)
                .map(user -> {
                    boolean isEmailChanged = request.getEmail() != null && !request.getEmail().equals(user.getEmail());
                    if (isEmailChanged && findByEmail(request.getEmail()).isPresent()) {
                        throw new EmailAlreadyExist("Try another email. This already exist");
                    }
                    userMapper.updateUserRequest(request, user);
                    return user;
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        updatedUser.setId(userId);
        updatedUser = userRepository.save(updatedUser);
        log.info("User {} updated", updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto findUserById(long userId) {
        log.info("Getting user with id: {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
