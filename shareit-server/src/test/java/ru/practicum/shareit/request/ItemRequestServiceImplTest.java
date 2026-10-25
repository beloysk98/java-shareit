package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserService userService;

    @Test
    void createRequest_success() {
        UserDto user = userService.create(UserDto.builder().name("Req").email("req@test.com").build());

        ItemRequestDto dto = ItemRequestDto.builder().description("Need a drill").build();
        ItemRequestResponseDto created = requestService.create(user.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getDescription()).isEqualTo("Need a drill");
        assertThat(created.getCreated()).isNotNull();
    }

    @Test
    void getAllByRequestor_success() {
        UserDto user = userService.create(UserDto.builder().name("Req2").email("req2@test.com").build());

        requestService.create(user.getId(), ItemRequestDto.builder().description("Item 1").build());
        requestService.create(user.getId(), ItemRequestDto.builder().description("Item 2").build());

        List<ItemRequestResponseDto> requests = requestService.getAllByRequestor(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Item 2");
    }

    @Test
    void getAllOther_success() {
        UserDto user1 = userService.create(UserDto.builder().name("U1").email("u1@test.com").build());
        UserDto user2 = userService.create(UserDto.builder().name("U2").email("u2@test.com").build());

        requestService.create(user1.getId(), ItemRequestDto.builder().description("For U1").build());
        requestService.create(user2.getId(), ItemRequestDto.builder().description("For U2").build());

        List<ItemRequestResponseDto> otherRequests = requestService.getAllOther(user1.getId());

        assertThat(otherRequests).hasSize(1);
        assertThat(otherRequests.get(0).getDescription()).isEqualTo("For U2");
    }

    @Test
    void getById_success() {
        UserDto u = userService.create(UserDto.builder().name("R").email("q1@t.com").build());
        var created = requestService.create(u.getId(),
                ItemRequestDto.builder().description("X").build());

        var found = requestService.getById(u.getId(), created.getId());
        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getById_notFound_throws() {
        UserDto u = userService.create(UserDto.builder().name("R").email("q2@t.com").build());
        assertThrows(ru.practicum.shareit.exception.NotFoundException.class,
                () -> requestService.getById(u.getId(), 999L));
    }
}