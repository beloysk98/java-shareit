package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private BookingService bookingService;

    @Test
    void create_returnsCreated() throws Exception {
        BookingResponseDto resp = BookingResponseDto.builder()
                .id(1L).status(BookingStatus.WAITING).build();
        when(bookingService.create(eq(1L), any(BookingRequestDto.class))).thenReturn(resp);

        BookingRequestDto req = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approve_returnsApproved() throws Exception {
        BookingResponseDto resp = BookingResponseDto.builder()
                .id(1L).status(BookingStatus.APPROVED).build();
        when(bookingService.approve(eq(1L), eq(1L), eq(true))).thenReturn(resp);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_returnsBooking() throws Exception {
        when(bookingService.getById(1L, 1L))
                .thenReturn(BookingResponseDto.builder().id(1L).build());

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllByBooker_returnsList() throws Exception {
        when(bookingService.getAllByBooker(eq(1L), eq("ALL")))
                .thenReturn(List.of(BookingResponseDto.builder().id(1L).build()));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllByOwner_returnsList() throws Exception {
        when(bookingService.getAllByOwner(eq(1L), eq("WAITING")))
                .thenReturn(List.of(BookingResponseDto.builder().id(2L).build()));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }
}