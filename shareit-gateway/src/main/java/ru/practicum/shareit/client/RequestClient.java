package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public Object create(Long userId, ItemRequestDto dto) {
        return post(API_PREFIX, userId, dto).getBody();
    }

    public Object getAllByRequestor(Long userId) {
        return get(API_PREFIX, userId).getBody();
    }

    public Object getAllOther(Long userId) {
        return get(API_PREFIX + "/all", userId).getBody();
    }

    public Object getById(Long userId, Long requestId) {
        return get(API_PREFIX + "/" + requestId, userId).getBody();
    }
}