package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serialize_deserialize() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder().description("Need drill").build();

        var written = json.write(dto);
        assertThat(written).extractingJsonPathStringValue("$.description").isEqualTo("Need drill");

        String content = "{\"description\":\"Need drill\"}";
        assertThat(json.parse(content).getObject().getDescription()).isEqualTo("Need drill");
    }
}