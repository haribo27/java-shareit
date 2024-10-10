package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void requestToUser_ShouldMapNewUserRequestDtoToUser() {
        // Arrange
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setName("Test User");

        // Act
        User user = userMapper.requestToUser(requestDto);

        // Assert
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
    }

    @Test
    void toUserDto_ShouldMapUserToUserDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");

        // Act
        UserDto userDto = userMapper.toUserDto(user);

        // Assert
        assertEquals(1L, userDto.getId());
        assertEquals("test@example.com", userDto.getEmail());
        assertEquals("Test User", userDto.getName());
    }

    @Test
    void updateUserRequest_ShouldUpdateUserFromUpdateUserRequestDto() {
        // Arrange
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setEmail("updated@example.com");

        User user = new User();
        user.setEmail("old@example.com");
        user.setName("Old User");

        // Act
        userMapper.updateUserRequest(requestDto, user);

        // Assert
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("Old User", user.getName()); // Name should not change
    }

    @Test
    void updateUserRequest_ShouldIgnoreNullValues() {
        // Arrange
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto();
        requestDto.setEmail(null); // Null value
        requestDto.setName("Updated User");

        User user = new User();
        user.setEmail("old@example.com");
        user.setName("Old User");

        // Act
        userMapper.updateUserRequest(requestDto, user);

        // Assert
        assertEquals("old@example.com", user.getEmail()); // Email should remain unchanged
        assertEquals("Updated User", user.getName()); // Name should be updated
    }
}
