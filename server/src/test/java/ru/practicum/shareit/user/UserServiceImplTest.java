package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void createAndGetUser_success() {
        UserDto dto = UserDto.builder().name("Ivan").email("ivan@test.com").build();
        UserDto created = userService.create(dto);

        assertThat(created.getId()).isNotNull();

        UserDto found = userService.getById(created.getId());
        assertThat(found.getEmail()).isEqualTo("ivan@test.com");
    }

    @Test
    void createUser_duplicateEmail_throwsConflict() {
        UserDto dto1 = UserDto.builder().name("A").email("dup@test.com").build();
        userService.create(dto1);

        UserDto dto2 = UserDto.builder().name("B").email("dup@test.com").build();
        assertThrows(ConflictException.class, () -> userService.create(dto2));
    }

    @Test
    void updateUser_success() {
        UserDto created = userService.create(UserDto.builder().name("Old").email("old@test.com").build());

        UserUpdateDto update = UserUpdateDto.builder().name("New").build();
        UserDto updated = userService.update(created.getId(), update);

        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getEmail()).isEqualTo("old@test.com"); // email не менялся
    }

    @Test
    void deleteUser_success() {
        UserDto created = userService.create(UserDto.builder().name("Del").email("del@test.com").build());
        userService.delete(created.getId());

        assertThrows(NotFoundException.class, () -> userService.getById(created.getId()));
    }

    @Test
    void getAll_returnsAll() {
        userService.create(UserDto.builder().name("A").email("r1@t.com").build());
        userService.create(UserDto.builder().name("B").email("r2@t.com").build());
        assertThat(userService.getAll()).hasSize(2);
    }

    @Test
    void updateUser_duplicateEmail_throwsConflict() {
        UserDto u1 = userService.create(UserDto.builder().name("A").email("s1@t.com").build());
        userService.create(UserDto.builder().name("B").email("s2@t.com").build());

        assertThrows(ConflictException.class, () -> userService.update(u1.getId(),
                UserUpdateDto.builder().email("s2@t.com").build()));
    }

    @Test
    void getById_notFound_throws() {
        assertThrows(NotFoundException.class, () -> userService.getById(999L));
    }
}