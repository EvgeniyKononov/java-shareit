package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor_IdOrderByCreationDateTimeDesc(Long userId);

    @Query("select p from ItemRequest p where p.requestor.id <> :id")
    Page<ItemRequest> findAllByRequestorIdWithoutRequester(Long id, Pageable pageRequest);
}
