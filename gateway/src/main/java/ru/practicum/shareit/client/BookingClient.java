package ru.practicum.shareit.client;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;
import java.util.Map;

public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(RestTemplate rest) {
        super(rest);
    }

    public Object create(Long userId, BookingRequestDto dto) {
        return post(API_PREFIX, userId, dto, Object.class).getBody();
    }

    public Object approve(Long bookingId, Boolean approved, Long userId) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(API_PREFIX + "/" + bookingId + "?approved={approved}", userId, parameters, null, Object.class)
                .getBody();
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