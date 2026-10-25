package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Test
    void createAndGetItem_success() {
        UserDto owner = userService.create(UserDto.builder().name("Owner").email("owner@test.com").build());

        ItemDto dto = ItemDto.builder()
                .name("Drill")
                .description("Bosch")
                .available(true)
                .build();

        ItemDto created = itemService.create(owner.getId(), dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Drill");
    }

    @Test
    void updateItem_onlyOwnerCanUpdate() {
        UserDto owner = userService.create(UserDto.builder().name("Owner").email("o@test.com").build());
        UserDto stranger = userService.create(UserDto.builder().name("Stranger").email("s@test.com").build());

        ItemDto created = itemService.create(owner.getId(), ItemDto.builder()
                .name("Item").description("Desc").available(true).build());

        ItemDto update = ItemDto.builder().name("Updated").build();

        assertThrows(NotFoundException.class, () -> itemService.update(stranger.getId(), created.getId(), update));

        ItemDto updated = itemService.update(owner.getId(), created.getId(), update);
        assertThat(updated.getName()).isEqualTo("Updated");
    }

    @Test
    void searchItem_success() {
        UserDto owner = userService.create(UserDto.builder().name("Owner").email("search@test.com").build());
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Hammer").description("Heavy tool").available(true).build());

        List<ItemDto> found = itemService.search("Hammer");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Hammer");
    }

    @Test
    void searchItem_emptyText_returnsEmptyList() {
        List<ItemDto> found = itemService.search("   ");
        assertThat(found).isEmpty();
    }

    @Test
    void getById_ownerSeesBookings_nonOwnerDoesNot() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("m1@t.com").build());
        UserDto other = userService.create(UserDto.builder().name("X").email("m2@t.com").build());
        ItemDto created = itemService.create(owner.getId(), ItemDto.builder()
                .name("I").description("D").available(true).build());

        var ownerView = itemService.getById(owner.getId(), created.getId());
        var otherView = itemService.getById(other.getId(), created.getId());

        assertThat(ownerView.getId()).isEqualTo(created.getId());
        assertThat(otherView.getLastBooking()).isNull();
        assertThat(otherView.getNextBooking()).isNull();
    }

    @Test
    void getByOwner_returnsOwnerItems() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("n1@t.com").build());
        itemService.create(owner.getId(), ItemDto.builder()
                .name("A").description("D").available(true).build());
        itemService.create(owner.getId(), ItemDto.builder()
                .name("B").description("D").available(true).build());

        assertThat(itemService.getByOwner(owner.getId())).hasSize(2);
    }

    @Test
    void addComment_noPastBooking_throwsValidation() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("p1@t.com").build());
        UserDto booker = userService.create(UserDto.builder().name("B").email("p2@t.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("I").description("D").available(true).build());

        assertThrows(ValidationException.class, () -> itemService.addComment(
                booker.getId(), item.getId(),
                ru.practicum.shareit.item.dto.CommentDto.builder().text("Nice").build()));
    }
}