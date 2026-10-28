package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemRequestDto dto) {
        return requestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllOther(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllOther(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        return requestService.getById(userId, requestId);
    }
}