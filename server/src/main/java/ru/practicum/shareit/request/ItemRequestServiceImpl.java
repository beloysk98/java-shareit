package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto create(Long userId, ItemRequestDto dto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();

        return ItemRequestMapper.toResponseDto(requestRepository.save(request), List.of());
    }

    @Override
    public List<ItemRequestResponseDto> getAllByRequestor(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(req -> ItemRequestMapper.toResponseDto(req, itemRepository.findAllByRequestId(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getAllOther(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId).stream()
                .map(req -> ItemRequestMapper.toResponseDto(req, itemRepository.findAllByRequestId(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        return ItemRequestMapper.toResponseDto(request, itemRepository.findAllByRequestId(requestId));
    }
}