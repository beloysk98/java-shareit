package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    @NotNull(message = "itemId обязателен")
    private Long itemId;

    @NotNull(message = "start обязателен")
    private LocalDateTime start;

    @NotNull(message = "end обязателен")
    private LocalDateTime end;
}