package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public Object create(Long userId, ItemDto itemDto) {
        return post(API_PREFIX, userId, itemDto).getBody();
    }

    public Object update(Long userId, Long itemId, ItemDto itemDto) {
        return patch(API_PREFIX + "/" + itemId, userId, itemDto).getBody();
    }

    public Object getById(Long userId, Long itemId) {
        return get(API_PREFIX + "/" + itemId, userId).getBody();
    }

    public Object getByOwner(Long userId) {
        return get(API_PREFIX, userId).getBody();
    }

    public Object search(String text) {
        return get(API_PREFIX + "/search", null, Map.of("text", text)).getBody();
    }

    public Object addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, commentDto).getBody();
    }
}