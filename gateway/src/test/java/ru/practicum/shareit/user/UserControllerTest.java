package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGatewayApp;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = ShareItGatewayApp.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserClient userClient;

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserDto dto = UserDto.builder().name("Name").email("test@test.com").build();
        UserDto returned = UserDto.builder().id(1L).name("Name").email("test@test.com").build();
        when(userClient.create(any())).thenReturn(returned);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createUser_invalidEmail_shouldReturnBadRequest() throws Exception {
        UserDto dto = UserDto.builder().name("Name").email("not-an-email").build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturnOk() throws Exception {
        UserUpdateDto dto = UserUpdateDto.builder().name("New").build();
        UserDto returned = UserDto.builder().id(1L).name("New").email("test@test.com").build();
        when(userClient.update(eq(1L), any(UserUpdateDto.class))).thenReturn(returned);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void getUserById_shouldReturnOk() throws Exception {
        UserDto returned = UserDto.builder().id(1L).name("Name").email("test@test.com").build();
        when(userClient.getById(1L)).thenReturn(returned);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        UserDto user1 = UserDto.builder().id(1L).name("Name").email("test@test.com").build();
        UserDto user2 = UserDto.builder().id(2L).name("Name2").email("test2@test.com").build();
        when(userClient.getAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        doNothing().when(userClient).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}