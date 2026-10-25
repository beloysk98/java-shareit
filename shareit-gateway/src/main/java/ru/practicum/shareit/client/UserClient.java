package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public Object create(UserDto userDto) {
        return post(API_PREFIX, null, userDto).getBody();
    }

    public Object update(Long userId, UserUpdateDto userDto) {
        return patch(API_PREFIX + "/" + userId, null, userDto).getBody();
    }

    public Object getById(Long userId) {
        return get(API_PREFIX + "/" + userId).getBody();
    }

    public Object getAll() {
        return get(API_PREFIX).getBody();
    }

    public void delete(Long userId) {
        super.delete(API_PREFIX + "/" + userId, null);
    }
}