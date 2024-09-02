package ru.practicum.shareit.user.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUser(NewUserRequestDto requestDto);

    UserDto toUserDto(User user);


    User updateUserRequest(UpdateUserRequestDto requestDto);
}
