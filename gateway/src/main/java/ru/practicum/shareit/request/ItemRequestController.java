package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@RequestHeader("X-Sharer-User-Id") Long userId,
                         @Valid @RequestBody ItemRequestDto dto) {
        return requestClient.create(userId, dto);
    }

    @GetMapping
    public Object getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllByRequestor(userId);
    }

    @GetMapping("/all")
    public Object getAllOther(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllOther(userId);
    }

    @GetMapping("/{requestId}")
    public Object getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long requestId) {
        return requestClient.getById(userId, requestId);
    }
}