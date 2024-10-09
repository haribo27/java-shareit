package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean // Изменено с @Autowired на @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private NewUserRequestDto newUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("user@example.com");
        userDto.setName("User");

        newUserRequestDto = new NewUserRequestDto();
        newUserRequestDto.setEmail("newuser@example.com");
        newUserRequestDto.setName("New User");

        updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setEmail("updated@example.com");
        updateUserRequestDto.setName(null); // Если name не нужно
    }

    @Test
    void findUserById_ExistingUser_ShouldReturnUserDto() throws Exception {
        doReturn(userDto).when(userService).findUserById(1L);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto))); // Проверяем JSON ответ
    }

    @Test
    void createUser_ValidRequest_ShouldReturnUserDto() throws Exception {
        doReturn(userDto).when(userService).createUser(any(NewUserRequestDto.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto))); // Проверяем JSON ответ
    }

    @Test
    void updateUser_ExistingUser_ShouldReturnUpdatedUserDto() throws Exception {
        doReturn(userDto).when(userService).updateUser(any(UpdateUserRequestDto.class), any(Long.class));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto))); // Проверяем JSON ответ
    }

    @Test
    void deleteUser_ExistingUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L); // Проверяем, что метод deleteUser был вызван
    }
}
