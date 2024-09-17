package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto createUser(NewUserRequestDto request);

    void deleteUser(long userId);

    UserDto updateUser(UpdateUserRequestDto request, long userId);

    UserDto findUserById(long userId);

}
