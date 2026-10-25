package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.client.BookingClient;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @Valid @RequestBody BookingRequestDto dto) {
        return bookingClient.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public Object approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long bookingId,
                          @RequestParam Boolean approved) {
        return bookingClient.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public Object getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public Object getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public Object getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllByOwner(userId, state);
    }
}