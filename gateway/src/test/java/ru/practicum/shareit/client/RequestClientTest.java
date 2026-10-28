package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
class RequestClientTest {

    private RestTemplate rest;
    private RequestClient client;

    @BeforeEach
    void setUp() {
        rest = mock(RestTemplate.class);
        client = new RequestClient(rest);
    }

    @Test
    void create_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(ItemRequestDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new ItemRequestDto()));

        client.create(1L, new ItemRequestDto());

        verify(rest).exchange(eq("/requests"), eq(HttpMethod.POST), any(), eq(ItemRequestDto.class), anyMap());
    }

    @Test
    void getAllByRequestor_shouldCallExchange() {
        when(rest.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        client.getAllByRequestor(1L);

        verify(rest).exchange(eq("/requests"), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void getAllOther_shouldCallExchange() {
        when(rest.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        client.getAllOther(1L);

        verify(rest).exchange(eq("/requests/all"), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    void getById_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(ItemRequestDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new ItemRequestDto()));

        client.getById(1L, 5L);

        verify(rest).exchange(eq("/requests/5"), eq(HttpMethod.GET), any(), eq(ItemRequestDto.class), anyMap());
    }
}