package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
class UserClientTest {

    private RestTemplate rest;
    private UserClient client;

    @BeforeEach
    void setUp() {
        rest = mock(RestTemplate.class);
        client = new UserClient(rest);
    }

    @Test
    void create_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(UserDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new UserDto()));

        client.create(new UserDto());

        verify(rest).exchange(eq("/users"), eq(HttpMethod.POST), any(), eq(UserDto.class), anyMap());
    }

    @Test
    void update_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(UserDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new UserDto()));

        client.update(1L, new UserUpdateDto());

        verify(rest).exchange(eq("/users/1"), eq(HttpMethod.PATCH), any(), eq(UserDto.class), anyMap());
    }

    @Test
    void getById_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(UserDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new UserDto()));

        client.getById(1L);

        verify(rest).exchange(eq("/users/1"), eq(HttpMethod.GET), any(), eq(UserDto.class), anyMap());
    }

    @Test
    void getAll_shouldCallExchange() {
        when(rest.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        client.getAll();

        verify(rest).exchange(eq("/users"), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void delete_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(Void.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());

        client.delete(1L);

        verify(rest).exchange(eq("/users/1"), eq(HttpMethod.DELETE), any(), eq(Void.class), anyMap());
    }
}