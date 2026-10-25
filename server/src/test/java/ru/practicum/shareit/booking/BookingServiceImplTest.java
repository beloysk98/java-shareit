package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    void createBooking_success() {
        UserDto owner = userService.create(UserDto.builder().name("Own").email("own@test.com").build());
        UserDto booker = userService.create(UserDto.builder().name("Book").email("book@test.com").build());

        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Item").description("Desc").available(true).build());

        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingResponseDto booking = bookingService.create(booker.getId(), dto);

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void createBooking_ownerCannotBookOwnItem() {
        UserDto owner = userService.create(UserDto.builder().name("Own").email("own2@test.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Item").description("Desc").available(true).build());

        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.create(owner.getId(), dto));
    }

    @Test
    void approveBooking_success() {
        UserDto owner = userService.create(UserDto.builder().name("Own").email("own3@test.com").build());
        UserDto booker = userService.create(UserDto.builder().name("Book").email("book3@test.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Item").description("Desc").available(true).build());

        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResponseDto booking = bookingService.create(booker.getId(), dto);

        BookingResponseDto approved = bookingService.approve(owner.getId(), booking.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_wrongUser_throwsValidation() {
        UserDto owner = userService.create(UserDto.builder().name("Own").email("own4@test.com").build());
        UserDto booker = userService.create(UserDto.builder().name("Book").email("book4@test.com").build());
        UserDto stranger = userService.create(UserDto.builder().name("Str").email("str4@test.com").build());

        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Item").description("Desc").available(true).build());

        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResponseDto booking = bookingService.create(booker.getId(), dto);

        assertThrows(ValidationException.class,
                () -> bookingService.approve(stranger.getId(), booking.getId(), true));
    }

    @Test
    void getAllByBooker_success() {
        UserDto owner = userService.create(UserDto.builder().name("Own").email("own5@test.com").build());
        UserDto booker = userService.create(UserDto.builder().name("Book").email("book5@test.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Item").description("Desc").available(true).build());

        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.create(booker.getId(), dto);

        List<BookingResponseDto> bookings = bookingService.getAllByBooker(booker.getId(), "ALL");
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getById_bookerCanSee() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("g1@t.com").build());
        UserDto booker = userService.create(UserDto.builder().name("B").email("g2@t.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("I").description("D").available(true).build());
        BookingResponseDto b = bookingService.create(booker.getId(), BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build());

        BookingResponseDto found = bookingService.getById(booker.getId(), b.getId());
        assertThat(found.getId()).isEqualTo(b.getId());
    }

    @Test
    void getById_strangerThrowsNotFound() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("h1@t.com").build());
        UserDto booker = userService.create(UserDto.builder().name("B").email("h2@t.com").build());
        UserDto stranger = userService.create(UserDto.builder().name("S").email("h3@t.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("I").description("D").available(true).build());
        BookingResponseDto b = bookingService.create(booker.getId(), BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build());

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(stranger.getId(), b.getId()));
    }

    @Test
    void getAllByOwner_success() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("i1@t.com").build());
        UserDto booker = userService.create(UserDto.builder().name("B").email("i2@t.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("I").description("D").available(true).build());
        bookingService.create(booker.getId(), BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).build());

        assertThat(bookingService.getAllByOwner(owner.getId(), "ALL")).hasSize(1);
    }

    @Test
    void getAllByBooker_unknownState_throwsValidation() {
        UserDto u = userService.create(UserDto.builder().name("U").email("j1@t.com").build());
        assertThrows(ValidationException.class,
                () -> bookingService.getAllByBooker(u.getId(), "WRONG"));
    }

    @Test
    void createBooking_unavailableItem_throwsValidation() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("k1@t.com").build());
        UserDto booker = userService.create(UserDto.builder().name("B").email("k2@t.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("I").description("D").available(false).build());

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(),
                BookingRequestDto.builder().itemId(item.getId())
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2)).build()));
    }

    @Test
    void createBooking_startAfterEnd_throwsValidation() {
        UserDto owner = userService.create(UserDto.builder().name("O").email("l1@t.com").build());
        UserDto booker = userService.create(UserDto.builder().name("B").email("l2@t.com").build());
        ItemDto item = itemService.create(owner.getId(), ItemDto.builder()
                .name("I").description("D").available(true).build());

        assertThrows(ValidationException.class, () -> bookingService.create(booker.getId(),
                BookingRequestDto.builder().itemId(item.getId())
                        .start(LocalDateTime.now().plusDays(3))
                        .end(LocalDateTime.now().plusDays(2)).build()));
    }
}