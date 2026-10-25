package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemClientTest {

    private RestTemplate rest;
    private ItemClient client;

    @BeforeEach
    void setUp() {
        rest = mock(RestTemplate.class);
        client = new ItemClient(rest);
    }

    @Test
    void create_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(ItemDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new ItemDto()));

        client.create(1L, new ItemDto());

        verify(rest).exchange(eq("/items"), eq(HttpMethod.POST), any(), eq(ItemDto.class), anyMap());
    }

    @Test
    void update_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(ItemDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new ItemDto()));

        client.update(1L, 5L, new ItemDto());

        verify(rest).exchange(eq("/items/5"), eq(HttpMethod.PATCH), any(), eq(ItemDto.class), anyMap());
    }

    @Test
    void getById_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(ItemDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new ItemDto()));

        client.getById(1L, 5L);

        verify(rest).exchange(eq("/items/5"), eq(HttpMethod.GET), any(), eq(ItemDto.class), anyMap());
    }

    @Test
    void getByOwner_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(java.util.List.class), anyMap()))
                .thenReturn(ResponseEntity.ok(java.util.List.of()));

        client.getByOwner(1L);

        verify(rest).exchange(eq("/items"), eq(HttpMethod.GET), any(), eq(java.util.List.class), anyMap());
    }

    @Test
    void search_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(java.util.List.class), anyMap()))
                .thenReturn(ResponseEntity.ok(java.util.List.of()));

        client.search("text");

        verify(rest).exchange(eq("/items/search"), eq(HttpMethod.GET), any(), eq(java.util.List.class), eq(Map.of("text", "text")));
    }

    @Test
    void addComment_shouldCallExchange() {
        when(rest.exchange(anyString(), any(), any(), eq(CommentDto.class), anyMap()))
                .thenReturn(ResponseEntity.ok(new CommentDto()));

        client.addComment(1L, 5L, new CommentDto());

        verify(rest).exchange(eq("/items/5/comment"), eq(HttpMethod.POST), any(), eq(CommentDto.class), anyMap());
    }
}