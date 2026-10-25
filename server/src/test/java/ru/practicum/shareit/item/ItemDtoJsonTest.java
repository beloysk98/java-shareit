package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serialize_withRequestId() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L).name("Drill").description("B").available(true).requestId(10L).build();
        var written = json.write(dto);
        assertThat(written).extractingJsonPathNumberValue("$.requestId").isEqualTo(10);
        assertThat(written).extractingJsonPathBooleanValue("$.available").isTrue();
    }

    @Test
    void deserialize_withoutRequestId() throws Exception {
        String content = "{\"name\":\"Drill\",\"description\":\"B\",\"available\":true}";
        assertThat(json.parse(content).getObject().getRequestId()).isNull();
    }
}