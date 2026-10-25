package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingClientTest {

    private RestTemplate rest;
    private BookingClient client;

    @BeforeEach
    void setUp() {
        rest = mock(RestTemplate.class);
        client = new BookingClient(rest);
    }

    @Test
    void create_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.create(1L, new BookingRequestDto());

        verify(rest).exchange(eq("/bookings"), eq(HttpMethod.POST), any(), eq(Object.class), anyMap());
    }

    @Test
    void approve_shouldCallExchangeWithQuery() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.approve(1L, 5L, true);

        verify(rest).exchange(eq("/bookings/5"), eq(HttpMethod.PATCH), any(), eq(Object.class), eq(Map.of("approved", true)));
    }

    @Test
    void getById_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.getById(1L, 5L);

        verify(rest).exchange(eq("/bookings/5"), eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }

    @Test
    void getAllByBooker_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.getAllByBooker(1L, "ALL");

        verify(rest).exchange(eq("/bookings"), eq(HttpMethod.GET), any(), eq(Object.class), eq(Map.of("state", "ALL")));
    }

    @Test
    void getAllByOwner_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new Object()));

        client.getAllByOwner(1L, "ALL");

        verify(rest).exchange(eq("/bookings/owner"), eq(HttpMethod.GET), any(), eq(Object.class), eq(Map.of("state", "ALL")));
    }
}