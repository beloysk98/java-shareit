package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public ItemClient(RestTemplate rest) {
        super(rest);
    }

    public ItemDto create(Long userId, ItemDto itemDto) {
        return post(API_PREFIX, userId, itemDto, ItemDto.class).getBody();
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        return patch(API_PREFIX + "/" + itemId, userId, itemDto, ItemDto.class).getBody();
    }

    public ItemDto getById(Long userId, Long itemId) {
        return get(API_PREFIX + "/" + itemId, userId, ItemDto.class).getBody();
    }

    @SuppressWarnings("unchecked")
    public List<ItemDto> getByOwner(Long userId) {
        return (List<ItemDto>) get(API_PREFIX, userId, List.class).getBody();
    }

    @SuppressWarnings("unchecked")
    public List<ItemDto> search(String text) {
        return (List<ItemDto>) get(API_PREFIX + "/search", null, Map.of("text", text), List.class).getBody();
    }

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, commentDto, CommentDto.class).getBody();
    }
}