package ru.practicum.shareit.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.user.UserClient;
import ru.practicum.shareit.gateway.user.UserController;
import ru.practicum.shareit.gateway.user.dto.NewUserRequestDto;
import ru.practicum.shareit.gateway.user.dto.UpdateUserRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    private NewUserRequestDto validNewUserRequestDto;
    private UpdateUserRequestDto validUpdateUserRequestDto;

    @BeforeEach
    void setUp() {
        validNewUserRequestDto = new NewUserRequestDto();
        validNewUserRequestDto.setName("John Doe");
        validNewUserRequestDto.setEmail("john.doe@example.com");

        validUpdateUserRequestDto = new UpdateUserRequestDto();
        validUpdateUserRequestDto.setName("John Updated");
        validUpdateUserRequestDto.setEmail("john.updated@example.com");
    }

    @Test
    void createUser_ValidRequest() throws Exception {
        Mockito.when(userClient.createUser(any(NewUserRequestDto.class)))
                .thenReturn(ResponseEntity.ok().body("User created"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNewUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User created"));
    }

    @Test
    void createUser_InvalidRequest() throws Exception {
        validNewUserRequestDto.setName("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNewUserRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_InvalidEmail() throws Exception {
        validNewUserRequestDto.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNewUserRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ValidRequest() throws Exception {
        Mockito.when(userClient.updateUser(any(UpdateUserRequestDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().body("User updated"));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User updated"));
    }

    @Test
    void updateUser_InvalidEmail() throws Exception {
        validUpdateUserRequestDto.setEmail("invalid-email");

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUpdateUserRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findUserById_ValidRequest() throws Exception {
        Mockito.when(userClient.findUserById(anyLong()))
                .thenReturn(ResponseEntity.ok().body("User found"));

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User found"));
    }
}
