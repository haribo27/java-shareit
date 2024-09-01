package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto request);

    void deleteUser(long userId);

    UserDto updateUser(UpdateUserRequestDto request, long userId);

    List<UserDto> getAllUsers();

    UserDto findUserById(long userId);

    boolean isEmailUnique(String email);
}
