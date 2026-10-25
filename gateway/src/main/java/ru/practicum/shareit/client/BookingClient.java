package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(RestTemplateBuilder builder, @Value("${shareit-server.url}") String serverUrl) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl)).build());
    }

    public BookingClient(RestTemplate rest) {
        super(rest);
    }

    public Object create(Long userId, BookingRequestDto dto) {
        return post(API_PREFIX, userId, dto, Object.class).getBody();
    }

    public Object approve(Long userId, Long bookingId, Boolean approved) {
        String path = API_PREFIX + "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null, Object.class).getBody();
    }

    public Object getById(Long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId, Object.class).getBody();
    }

    public List<Object> getAllByBooker(Long userId, String state) {
        return getList(API_PREFIX + "?state=" + state, userId, Object.class);
    }

    public List<Object> getAllByOwner(Long userId, String state) {
        return getList(API_PREFIX + "/owner?state=" + state, userId, Object.class);
    }
}