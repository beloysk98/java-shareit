package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ItemService itemService;

    @Test
    void createItem_shouldReturnCreated() throws Exception {
        ItemDto dto = ItemDto.builder().name("Test").description("Desc").available(true).build();
        ItemDto response = ItemDto.builder().id(1L).name("Test").description("Desc").available(true).build();
        when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void updateItem_shouldReturnUpdated() throws Exception {
        ItemDto dto = ItemDto.builder().name("Upd").build();
        ItemDto response = ItemDto.builder().id(1L).name("Upd").build();
        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(response);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Upd"));
    }

    @Test
    void getById_shouldReturnItemResponse() throws Exception {
        ItemResponseDto response = ItemResponseDto.builder().id(1L).name("Item").build();
        when(itemService.getById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getByOwner_shouldReturnList() throws Exception {
        when(itemService.getByOwner(1L)).thenReturn(List.of(
                ItemResponseDto.builder().id(1L).build(),
                ItemResponseDto.builder().id(2L).build()));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void searchItems_shouldReturnList() throws Exception {
        when(itemService.search("Test")).thenReturn(List.of(ItemDto.builder().id(1L).build()));

        mockMvc.perform(get("/items/search").param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentDto req = CommentDto.builder().text("Nice").build();
        CommentDto resp = CommentDto.builder().id(1L).text("Nice")
                .authorName("Name").created(LocalDateTime.now()).build();
        when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(resp);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Nice"));
    }
}