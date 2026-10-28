package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void serialize_deserialize() throws Exception {
        UserDto dto = UserDto.builder().id(1L).name("Ivan").email("i@t.com").build();

        var written = json.write(dto);
        assertThat(written).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(written).extractingJsonPathStringValue("$.email").isEqualTo("i@t.com");

        String content = "{\"id\":1,\"name\":\"Ivan\",\"email\":\"i@t.com\"}";
        assertThat(json.parse(content).getObject().getName()).isEqualTo("Ivan");
    }
}