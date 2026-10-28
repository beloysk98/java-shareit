package ru.practicum.shareit.client;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

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
        return (List<ItemDto>) get(API_PREFIX + "/search?text={text}", null, Map.of("text", text), List.class)
                .getBody();
    }

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        return post(API_PREFIX + "/" + itemId + "/comment", userId, commentDto, CommentDto.class).getBody();
    }
}