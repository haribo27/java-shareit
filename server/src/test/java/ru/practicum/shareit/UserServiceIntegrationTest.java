package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper; // для сериализации объектов в JSON

    private User user;

    @BeforeEach
    void setUp() {
        // Инициализация объекта пользователя
        user = new User();
        user.setEmail("user@example.com");
        user.setName("User");
        userRepository.save(user);
    }

    @Test
    @Transactional
    void createUser_ValidRequest_ShouldReturnUserDto() throws Exception {
        NewUserRequestDto newUserRequest = new NewUserRequestDto();
        newUserRequest.setName("New User");
        newUserRequest.setEmail("newuser@example.com");

        mockMvc.perform(post("/users") // Убедитесь, что этот путь соответствует вашему контроллеру
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isOk());

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2); // Проверяем, что два пользователя теперь в базе данных
    }

    @Test
    @Transactional
    void updateUser_ValidRequest_ShouldReturnUpdatedUserDto() throws Exception {
        UpdateUserRequestDto updateUserRequest = new UpdateUserRequestDto();
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setName(null);

        mockMvc.perform(patch("/users/" + user.getId()) // Убедитесь, что этот путь соответствует вашему контроллеру
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @Transactional
    void findUserById_ExistingUser_ShouldReturnUserDto() throws Exception {
        mockMvc.perform(get("/users/" + user.getId()) // Убедитесь, что этот путь соответствует вашему контроллеру
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void deleteUser_ExistingUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/" + user.getId()) // Убедитесь, что этот путь соответствует вашему контроллеру
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId())).isEmpty(); // Проверяем, что пользователь был удален
    }

    @Test
    @Transactional
    void createUser_EmailAlreadyExists_ShouldReturnConflict() throws Exception {
        NewUserRequestDto newUserRequest = new NewUserRequestDto();
        newUserRequest.setEmail("user@example.com");
        newUserRequest.setName("Duplicate User");

        mockMvc.perform(post("/users") // Убедитесь, что этот путь соответствует вашему контроллеру
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isConflict()); // Проверяем, что возвращается статус 409
    }
}
