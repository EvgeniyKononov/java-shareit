package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto);

    List<ItemRequestDto> findAllByUserId(Long userId);

    List<ItemRequestDto> findAll(Long userId, Pageable pageRequest);

    ItemRequestDto findById(Long userId, Long requestId);
}
