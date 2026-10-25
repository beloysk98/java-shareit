package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BaseClientTest {

    private RestTemplate rest;
    private BaseClient client;

    @BeforeEach
    void setUp() {
        rest = mock(RestTemplate.class);
        client = new BaseClient(rest);
    }

    @Test
    void get_withoutUserId_shouldCallExchange() {
        when(rest.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.get("/test", Object.class);

        verify(rest).exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }

    @Test
    void get_withUserId_shouldSetHeader() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.get("/test", 42L, Object.class);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(rest).exchange(eq("/test"), eq(HttpMethod.GET), captor.capture(), eq(Object.class), anyMap());

        assertEquals("42", captor.getValue().getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void get_withParameters_shouldPassParameters() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.get("/test", 1L, Map.of("text", "abc"), Object.class);

        verify(rest).exchange(eq("/test"), eq(HttpMethod.GET), any(), eq(Object.class), eq(Map.of("text", "abc")));
    }

    @Test
    void post_shouldSendBodyAndHeader() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.post("/test", 1L, Map.of("a", 1), Object.class);

        verify(rest).exchange(eq("/test"), eq(HttpMethod.POST), any(), eq(Object.class), anyMap());
    }

    @Test
    void patch_withoutParameters_shouldWork() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.patch("/test", 1L, Map.of("k", "v"), Object.class);

        verify(rest).exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class), anyMap());
    }

    @Test
    void patch_withParameters_shouldWork() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.patch("/test", 1L, Map.of("approved", true), null, Object.class);

        verify(rest).exchange(eq("/test"), eq(HttpMethod.PATCH), any(), eq(Object.class), eq(Map.of("approved", true)));
    }

    @Test
    void delete_shouldWork() {
        when(rest.exchange(anyString(), any(), any(), eq(Void.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());

        client.delete("/test", 1L, Void.class);

        verify(rest).exchange(eq("/test"), eq(HttpMethod.DELETE), any(), eq(Void.class), anyMap());
    }
}