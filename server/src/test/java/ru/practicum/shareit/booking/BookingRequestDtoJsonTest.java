package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testSerializeAndDeserializeDates() throws Exception {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2025, 1, 1, 10, 0))
                .end(LocalDateTime.of(2025, 1, 2, 10, 0))
                .build();

        var written = json.write(dto);
        assertThat(written).hasJsonPathNumberValue("$.itemId");
        assertThat(written).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T10:00:00");

        String content = "{\"itemId\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\"}";
        var parsed = json.parse(content);
        assertThat(parsed.getObject().getItemId()).isEqualTo(1L);
        assertThat(parsed.getObject().getStart()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
    }
}