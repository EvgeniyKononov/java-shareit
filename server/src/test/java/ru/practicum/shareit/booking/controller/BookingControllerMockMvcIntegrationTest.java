package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerMockMvcIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private Long userId;
    private Long bookingId;
    private BookingInputDto inputDto;
    private BookingOutputDto outputDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        bookingId = 1L;
        LocalDateTime current = LocalDateTime.now();
        inputDto = BookingInputDto.builder()
                .start(current.plusDays(1))
                .end(current.plusDays(2))
                .build();
        outputDto = new BookingOutputDto();
    }

    @SneakyThrows
    @Test
    void create_whenInvokeWithDto_thenStatusOkAndReturnDto() {
        when(bookingService.createBooking(userId, inputDto)).thenReturn(outputDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(outputDto), result);
    }

    @SneakyThrows
    @Test
    void amend_whenInvokeWithDto_thenStatusOkAndReturnDto() {
        boolean approved = true;
        when(bookingService.updateBooking(userId, bookingId, approved)).thenReturn(outputDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(outputDto), result);
    }

    @SneakyThrows
    @Test
    void find_whenInvoke_thenReturnDtoAndStatusOk() {
        when(bookingService.findBooking(userId, bookingId)).thenReturn(outputDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(outputDto), result);
    }

    @SneakyThrows
    @Test
    void findAllByBooker_whenInvoke_thenReturnListDtoAndStatusOk() {
        String state = "ALL";
        BookingOutputDto outputDto2 = new BookingOutputDto();
        List<BookingOutputDto> bookingOutputDtoList = new ArrayList<>(List.of(outputDto, outputDto2));
        when(bookingService.findAllBookingsByBookerId(userId, state, PageRequest.of(0, 10)))
                .thenReturn(bookingOutputDtoList);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutputDtoList), result);
    }

    @SneakyThrows
    @Test
    void findAllByOwner_whenInvoke_thenReturnListDtoAndStatusOk() {
        String state = "ALL";
        BookingOutputDto outputDto2 = new BookingOutputDto();
        List<BookingOutputDto> bookingOutputDtoList = new ArrayList<>(List.of(outputDto, outputDto2));
        when(bookingService.findAllBookingsByOwnerId(userId, state, PageRequest.of(0, 10)))
                .thenReturn(bookingOutputDtoList);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingOutputDtoList), result);
    }
}