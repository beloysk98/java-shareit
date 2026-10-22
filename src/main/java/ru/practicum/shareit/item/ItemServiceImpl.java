package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto dto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(dto, owner, null)));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto dto) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!existing.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать может только владелец");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            existing.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            existing.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            existing.setAvailable(dto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existing));
    }

    @Override
    public ItemResponseDto getById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        ItemResponseDto dto = ItemMapper.toResponseDto(item);
        dto.setComments(commentRepository.findAllByItemIdOrderByCreatedAsc(itemId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));
        if (item.getOwner().getId().equals(userId)) {
            dto.setLastBooking(lastBooking(itemId));
            dto.setNextBooking(nextBooking(itemId));
        }
        return dto;
    }

    @Override
    public List<ItemResponseDto> getByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<CommentDto>> commentsMap = commentRepository
                .findAllByItemIdInOrderByCreatedAsc(itemIds).stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId(),
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())));

        Map<Long, BookingShortDto> lastBookingsMap = new HashMap<>();
        bookingRepository.findAllPastByItemIds(itemIds, BookingStatus.APPROVED, now)
                .forEach(b -> lastBookingsMap.putIfAbsent(b.getItem().getId(), toShortDto(b)));

        Map<Long, BookingShortDto> nextBookingsMap = new HashMap<>();
        bookingRepository.findAllNextByItemIds(itemIds, BookingStatus.APPROVED, now)
                .forEach(b -> nextBookingsMap.putIfAbsent(b.getItem().getId(), toShortDto(b)));

        return items.stream().map(item -> {
            ItemResponseDto dto = ItemMapper.toResponseDto(item);
            dto.setLastBooking(lastBookingsMap.get(item.getId()));
            dto.setNextBooking(nextBookingsMap.get(item.getId()));
            dto.setComments(commentsMap.getOrDefault(item.getId(), List.of()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hadBooking = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hadBooking) {
            throw new ValidationException("Оставить комментарий можно только после аренды вещи");
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private BookingShortDto lastBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now())
                .map(this::toShortDto)
                .orElse(null);
    }

    private BookingShortDto nextBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        itemId, BookingStatus.APPROVED, LocalDateTime.now())
                .map(this::toShortDto)
                .orElse(null);
    }

    private BookingShortDto toShortDto(Booking b) {
        return BookingShortDto.builder()
                .id(b.getId())
                .bookerId(b.getBooker().getId())
                .start(b.getStart())
                .end(b.getEnd())
                .build();
    }
}