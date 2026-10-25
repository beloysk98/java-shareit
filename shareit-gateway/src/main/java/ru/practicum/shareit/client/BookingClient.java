package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public Object create(Long userId, BookingRequestDto dto) {
        return post(API_PREFIX, userId, dto, Object.class).getBody();
    }

    public Object approve(Long userId, Long bookingId, Boolean approved) {
        return patch(API_PREFIX + "/" + bookingId, userId, Map.of("approved", approved), null, Object.class).getBody();
    }

    public Object getById(Long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId, Object.class).getBody();
    }

    public Object getAllByBooker(Long userId, String state) {
        return get(API_PREFIX, userId, Map.of("state", state), Object.class).getBody();
    }

    public Object getAllByOwner(Long userId, String state) {
        return get(API_PREFIX + "/owner", userId, Map.of("state", state), Object.class).getBody();
    }
}