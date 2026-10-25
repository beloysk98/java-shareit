package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        return post(API_PREFIX, userId, dto, ItemRequestDto.class).getBody();
    }

    @SuppressWarnings("unchecked")
    public List<ItemRequestDto> getAllByRequestor(Long userId) {
        return (List<ItemRequestDto>) get(API_PREFIX, userId, List.class).getBody();
    }

    @SuppressWarnings("unchecked")
    public List<ItemRequestDto> getAllOther(Long userId) {
        return (List<ItemRequestDto>) get(API_PREFIX + "/all", userId, List.class).getBody();
    }

    public ItemRequestDto getById(Long userId, Long requestId) {
        return get(API_PREFIX + "/" + requestId, userId, ItemRequestDto.class).getBody();
    }
}