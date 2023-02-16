package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse commentCreate(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Positive @PathVariable Long itemId,
                                            @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return itemService.commentCreate(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse get(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                               @Positive @PathVariable Long itemId) {
        return itemService.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoResponse> getUserItems(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(
            @RequestParam(value = "text") String searchText,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return itemService.getSearchItems(searchText, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                          @Positive @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                       @Positive @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }
}
