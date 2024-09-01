package ru.practicum.shareit.user.mapper;


import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    public static User mapToUser(NewUserRequestDto request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User updateUserFields(User user, UpdateUserRequestDto request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return user;
    }
}
