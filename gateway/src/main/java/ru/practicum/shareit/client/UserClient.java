package ru.practicum.shareit.client;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(RestTemplate rest) {
        super(rest);
    }

    public UserDto create(UserDto userDto) {
        return post(API_PREFIX, null, userDto, UserDto.class).getBody();
    }

    public UserDto update(Long userId, UserUpdateDto userDto) {
        return patch(API_PREFIX + "/" + userId, null, userDto, UserDto.class).getBody();
    }

    public UserDto getById(Long userId) {
        return get(API_PREFIX + "/" + userId, UserDto.class).getBody();
    }

    public List<UserDto> getAll() {
        return getList(API_PREFIX, null, UserDto.class);
    }

    public void delete(Long userId) {
        super.delete(API_PREFIX + "/" + userId, null, Void.class);
    }
}