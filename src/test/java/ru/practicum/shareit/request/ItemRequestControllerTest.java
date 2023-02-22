package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    private static final String PATH = "/requests";
    private static final String PATH_WITH_ID = "/requests/1";
    private static final String PATH_WITH_ALL = "/requests/all";

    @MockBean
    ItemRequestService requestService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    ItemRequestDto request = new ItemRequestDto(
            1L,
            "request description",
            new User(1L, "user", "m@m.com"),
            LocalDateTime.of(2023, 1, 1, 1, 1, 0),
            new ArrayList<>()
    );

    @Test
    void testCreateOk() throws Exception {
        when(requestService.create(1L, request))
                .thenReturn(request);

        mvc.perform(MockMvcRequestBuilders.post(PATH)
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.created", is("2023-01-01T01:01:00")))
                .andExpect(jsonPath("$.items", is(request.getItems())));

        verify(requestService, times(1))
                .create(1L, request);
    }

    @Test
    void testCreateRequestEmptyDescription() throws Exception {
        ItemRequestDto requestEmpty = new ItemRequestDto(
                1L,
                "",
                new User(1L, "user", "m@m.com"),
                LocalDateTime.of(2023, 1, 1, 1, 1, 0),
                new ArrayList<>()
        );

        when(requestService.create(1L, requestEmpty))
                .thenThrow(new BadRequestException("Invalid description"));

        mvc.perform(MockMvcRequestBuilders.post(PATH)
                        .content(mapper.writeValueAsString(requestEmpty))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException));

        verify(requestService, never())
                .create(any(), any());
    }

    @Test
    void testGetRequestById() throws Exception {
        when(requestService.getRequest(anyLong(), anyLong()))
                .thenReturn(request);

        mvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requestor.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.created", is("2023-01-01T01:01:00")))
                .andExpect(jsonPath("$.items", is(request.getItems())));

        verify(requestService, times(1))
                .getRequest(anyLong(), anyLong());
    }

    @Test
    void testGetUserRequests() throws Exception {
        when(requestService.getUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(request));

        mvc.perform(MockMvcRequestBuilders.get(PATH)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is("2023-01-01T01:01:00")))
                .andExpect(jsonPath("$[0].items", is(request.getItems())));

        verify(requestService, times(1))
                .getUserRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    void testGetAllRequests() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(request));

        mvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ALL)
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].requestor.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].created", is("2023-01-01T01:01:00")))
                .andExpect(jsonPath("$[0].items", is(request.getItems())));

        verify(requestService, times(1))
                .getAllRequests(anyLong(), anyInt(), anyInt());
    }
}