package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    private static final String PATH = "/bookings";
    private static final String PATH_WITH_ID = "/bookings/1";
    private static final String PATH_WITH_OWNER = "/bookings/owner";
    private static final String PATH_WITH_APPROVED = "/bookings/1?approved=true";

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    BookingDto booking = new BookingDto(
            1L,
            LocalDateTime.of(2024, 1, 1, 1, 1, 0),
            LocalDateTime.of(2025, 1, 1, 1, 1, 0),
            1L,
            1L,
            Status.WAITING
    );

    BookingDtoResponse bookingResponse = new BookingDtoResponse(
            1L,
            LocalDateTime.of(2024, 1, 1, 1, 1, 0),
            LocalDateTime.of(2025, 1, 1, 1, 1, 0),
            Item.builder().id(1L).build(),
            User.builder().id(1L).build(),
            Status.WAITING
    );

    @Test
    void testCreateOk() throws Exception {
        when(bookingService.create(1L, booking))
                .thenReturn(bookingResponse);

        mvc.perform(MockMvcRequestBuilders.post(PATH)
                        .content(mapper.writeValueAsString(booking))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is("2024-01-01T01:01:00")))
                .andExpect(jsonPath("$.end", is("2025-01-01T01:01:00")))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())));

        verify(bookingService, times(1))
                .create(1L, booking);
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is("2024-01-01T01:01:00")))
                .andExpect(jsonPath("$.end", is("2025-01-01T01:01:00")))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())));

        verify(bookingService, times(1))
                .get(anyLong(), anyLong());
    }

    @Test
    void testGetBookingsByBooker() throws Exception {
        when(bookingService.getByBooker(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is("2024-01-01T01:01:00")))
                .andExpect(jsonPath("$[0].end", is("2025-01-01T01:01:00")))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.getStatus().toString())));

        verify(bookingService, times(1))
                .getByBooker(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void testGetBookingsByOwner() throws Exception {
        when(bookingService.getByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponse));

        mvc.perform(MockMvcRequestBuilders.get(PATH_WITH_OWNER)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is("2024-01-01T01:01:00")))
                .andExpect(jsonPath("$[0].end", is("2025-01-01T01:01:00")))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.getStatus().toString())));

        verify(bookingService, times(1))
                .getByOwner(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void testConfirmBooking() throws Exception {
        bookingResponse.setStatus(Status.APPROVED);

        when(bookingService.confirm(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_APPROVED)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())));

        verify(bookingService, times(1))
                .confirm(anyLong(), anyLong(), anyBoolean());

    }
}