package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    private static final String PATH = "/items";
    private static final String PATH_WITH_ID = "/items/1";
    private static final String PATH_WITH_COMMENT = "/items/1/comment";
    private static final String PATH_WITH_SEARCH = "/items/search?text=search";

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    ItemDto item = new ItemDto(
            1L,
            "item",
            "item description",
            true,
            1L
    );

    ItemDtoResponse itemResponse = new ItemDtoResponse(
            1L,
            "item",
            "item description",
            true,
            new BookingDto(),
            new BookingDto(),
            new ArrayList<>(),
            null
    );

    CommentDto comment = new CommentDto(
            1L,
            "comment text",
            1L,
            1L
    );

    CommentDtoResponse commentResponse = new CommentDtoResponse(
            1L,
            "comment text",
            Item.builder().id(1L).build(),
            "author",
            LocalDateTime.of(2023, 1, 1, 1, 1, 0)

    );

    @Test
    void testCreateItemOk() throws Exception {
        when(itemService.create(1L, item))
                .thenReturn(item);

        mvc.perform(MockMvcRequestBuilders.post(PATH)
                                          .content(mapper.writeValueAsString(item))
                                          .header("X-Sharer-User-Id", 1L)
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(item.getName())))
           .andExpect(jsonPath("$.description", is(item.getDescription())))
           .andExpect(jsonPath("$.available", is(item.getAvailable())))
           .andExpect(jsonPath("$.requestId", is(1)));

        verify(itemService, times(1))
                .create(1L, item);
    }

    @Test
    void testCreateItemEmptyName() throws Exception {
        ItemDto itemEmpty = new ItemDto(
                1L,
                "",
                "item description",
                true,
                1L
        );

        when(itemService.create(1L, itemEmpty))
                .thenThrow(new BadRequestException("Invalid name"));

        mvc.perform(MockMvcRequestBuilders.post(PATH)
                                          .content(mapper.writeValueAsString(itemEmpty))
                                          .header("X-Sharer-User-Id", 1L)
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest())
           .andExpect(result -> assertTrue(result.getResolvedException()
                   instanceof MethodArgumentNotValidException));

        verify(itemService, never())
                .create(any(), any());
    }

    @Test
    void testCreateCommentOk() throws Exception {
        when(itemService.commentCreate(1L, 1L, comment))
                .thenReturn(commentResponse);

        mvc.perform(MockMvcRequestBuilders.post(PATH_WITH_COMMENT)
                                          .content(mapper.writeValueAsString(comment))
                                          .header("X-Sharer-User-Id", 1L)
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
           .andExpect(jsonPath("$.text", is(commentResponse.getText())))
           .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())))
           .andExpect(jsonPath("$.created", is("2023-01-01T01:01:00")));

        verify(itemService, times(1))
                .commentCreate(1L, 1L, comment);
    }

    @Test
    void testCreateCommentEmptyText() throws Exception {
        CommentDto commentEmpty = new CommentDto(
                1L,
                "",
                1L,
                1L
        );

        when(itemService.commentCreate(1L, 1L, commentEmpty))
                .thenThrow(new BadRequestException("Invalid text"));

        mvc.perform(MockMvcRequestBuilders.post(PATH_WITH_COMMENT)
                                          .content(mapper.writeValueAsString(commentEmpty))
                                          .header("X-Sharer-User-Id", 1L)
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest())
           .andExpect(result -> assertTrue(result.getResolvedException()
                   instanceof MethodArgumentNotValidException));

        verify(itemService, never())
                .commentCreate(1L, 1L, commentEmpty);
    }

    @Test
    void testGetItemById() throws Exception {
        when(itemService.get(anyLong(), anyLong()))
                .thenReturn(itemResponse);

        mvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                                          .header("X-Sharer-User-Id", 1L)
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(itemResponse.getName())))
           .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
           .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));

        verify(itemService, times(1))
                .get(anyLong(), anyLong());
    }

    @Test
    void testGetUserItems() throws Exception {
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponse));

        mvc.perform(MockMvcRequestBuilders.get(PATH)
                                          .header("X-Sharer-User-Id", 1L)
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].id", is(itemResponse.getId()), Long.class))
           .andExpect(jsonPath("$[0].name", is(itemResponse.getName())))
           .andExpect(jsonPath("$[0].description", is(itemResponse.getDescription())))
           .andExpect(jsonPath("$[0].available", is(itemResponse.getAvailable())));

        verify(itemService, times(1))
                .getUserItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void testGetSearchItems() throws Exception {
        when(itemService.getSearchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(MockMvcRequestBuilders.get(PATH_WITH_SEARCH)
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
           .andExpect(jsonPath("$[0].name", is(item.getName())))
           .andExpect(jsonPath("$[0].description", is(item.getDescription())))
           .andExpect(jsonPath("$[0].available", is(item.getAvailable())))
           .andExpect(jsonPath("$[0].requestId", is(item.getId()), Long.class));

        verify(itemService, times(1))
                .getSearchItems(anyString(), anyInt(), anyInt());
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto updateItem = new ItemDto(
                1L,
                "Vasiliy Fortochkin's item",
                "Vasiliy Fortochkin's item description",
                true,
                1L
        );

        when(itemService.update(1L, 1L, updateItem))
                .thenReturn(updateItem);

        mvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                                          .header("X-Sharer-User-Id", 1L)
                                          .content(mapper.writeValueAsString(updateItem))
                                          .characterEncoding(StandardCharsets.UTF_8)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(updateItem.getId()), Long.class))
           .andExpect(jsonPath("$.name", is(updateItem.getName())))
           .andExpect(jsonPath("$.description", is(updateItem.getDescription())));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void testDeleteItemById() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(PATH_WITH_ID)
                                          .header("X-Sharer-User-Id", 1L))
           .andExpect(status().isOk());
    }
}