package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundRequestException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.Answer;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.constant.Constant.*;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final JpaUserRepository userRepository;
    private final JpaItemRepository itemRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, JpaUserRepository userRepository, JpaItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID));
        ItemRequest request = RequestMapper.toNewEntity(requestDto, requestor);
        ArrayList<Answer> answers = new ArrayList<>();
        return RequestMapper.toDto(requestRepository.save(request), answers);
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(Long userId) {
        List<ItemRequest> requests = requestRepository.findAllByRequestor_IdOrderByCreationDateTimeDesc(userId);
        if (requests.isEmpty()) {
            checkUser(userId);
        }
        return getRequestDtos(requests);
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, Pageable pageRequest) {
        Page<ItemRequest> page = requestRepository.findAllByRequestorIdWithoutRequester(userId, pageRequest);
        List<ItemRequest> requests = page.getContent();
        if (requests.isEmpty()) {
            checkUser(userId);
        }
        return getRequestDtos(requests);
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        checkUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundRequestException(NOT_FOUND_REQUEST_MSG));
        return RequestMapper.toDto(request, getAnswers(request));
    }

    private List<ItemRequestDto> getRequestDtos(List<ItemRequest> requests) {
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestDtos.add(RequestMapper.toDto(request, getAnswers(request)));
        }
        return requestDtos;
    }

    private List<Answer> getAnswers(ItemRequest request) {
        List<Answer> answers = new ArrayList<>();
        List<Item> items = itemRepository.findAllByRequest_Id(request.getId());
        for (Item item : items) {
            answers.add(getAnswerFromItem(item));
        }
        return answers;
    }

    private Answer getAnswerFromItem(Item item) {
        return new Answer(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID));
    }
}
