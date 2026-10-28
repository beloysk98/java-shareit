package ru.practicum.shareit.client;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(RestTemplate rest) {
        super(rest);
    }

    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        return post(API_PREFIX, userId, dto, ItemRequestDto.class).getBody();
    }

    public List<ItemRequestDto> getAllByRequestor(Long userId) {
        return getList(API_PREFIX, userId, ItemRequestDto.class);
    }

    public List<ItemRequestDto> getAllOther(Long userId) {
        return getList(API_PREFIX + "/all", userId, ItemRequestDto.class);
    }

    public ItemRequestDto getById(Long userId, Long requestId) {
        return get(API_PREFIX + "/" + requestId, userId, ItemRequestDto.class).getBody();
    }
}