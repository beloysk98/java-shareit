package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.ShareItGatewayApp;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = ShareItGatewayApp.class)
class ItemControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ItemClient itemClient;

    @Test
    void createItem_shouldReturnCreated() throws Exception {
        ItemDto dto = ItemDto.builder().name("Test").description("Desc").available(true).build();
        when(itemClient.create(eq(1L), any(ItemDto.class))).thenReturn(Map.of("id", 1));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createItem_blankName_shouldReturnBadRequest() throws Exception {
        ItemDto dto = ItemDto.builder().name("").description("Desc").available(true).build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_shouldReturnOk() throws Exception {
        ItemDto dto = ItemDto.builder().name("Upd").build();
        when(itemClient.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(Map.of("name", "Upd"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Upd"));
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(itemClient.getById(1L, 1L)).thenReturn(Map.of("id", 1));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByOwner_shouldReturnOk() throws Exception {
        when(itemClient.getByOwner(1L)).thenReturn(List.of(Map.of("id", 1)));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchItems_shouldReturnOk() throws Exception {
        when(itemClient.search("Test")).thenReturn(List.of(Map.of("id", 1)));

        mockMvc.perform(get("/items/search").param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void addComment_shouldReturnOk() throws Exception {
        CommentDto req = CommentDto.builder().text("Nice").build();
        when(itemClient.addComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(Map.of("text", "Nice"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Nice"));
    }

    @Test
    void addComment_blankText_shouldReturnBadRequest() throws Exception {
        CommentDto req = CommentDto.builder().text("").build();

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}