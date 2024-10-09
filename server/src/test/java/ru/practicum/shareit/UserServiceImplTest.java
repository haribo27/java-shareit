package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.EmailAlreadyExist;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;
    private NewUserRequestDto newUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Prepare test data
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        newUserRequestDto = new NewUserRequestDto();
        newUserRequestDto.setEmail("test@example.com");
        newUserRequestDto.setName("Test User");

        updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setEmail("updated@example.com");
        updateUserRequestDto.setName("Updated User");
    }

    @Test
    void createUser_ValidRequest_ShouldReturnUserDto() {
        // Arrange
        when(userMapper.requestToUser(any())).thenReturn(user);
        when(userMapper.toUserDto(any())).thenReturn(userDto);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);

        // Act
        UserDto createdUser = userService.createUser(newUserRequestDto);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ShouldThrowEmailAlreadyExist() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(EmailAlreadyExist.class, () -> userService.createUser(newUserRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ExistingUser_ShouldDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NonExistingUser_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void updateUser_ExistingUser_ShouldReturnUpdatedUserDto() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(any())).thenReturn(userDto);

        // Act
        UserDto updatedUser = userService.updateUser(updateUserRequestDto, 1L);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(user.getId(), updatedUser.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NonExistingUser_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(updateUserRequestDto, 1L));
    }

    @Test
    void updateUser_EmailAlreadyExists_ShouldThrowEmailAlreadyExist() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        updateUserRequestDto.setEmail("another@example.com");
        assertThrows(EmailAlreadyExist.class, () -> userService.updateUser(updateUserRequestDto, 1L));
    }

    @Test
    void findUserById_ExistingUser_ShouldReturnUserDto() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(any())).thenReturn(userDto);

        // Act
        UserDto foundUser = userService.findUserById(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void findUserById_NonExistingUser_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1L));
    }
}
