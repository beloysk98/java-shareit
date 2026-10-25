package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

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