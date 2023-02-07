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
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse commentCreate(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                            @PathVariable Long itemId,
                                            @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return itemService.commentCreate(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse get(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                               @PathVariable Long itemId) {
        return itemService.get(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoResponse> getUserItems(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(@RequestParam(value = "text") String searchText) {
        return itemService.getSearchItems(searchText);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") @Positive Long userId, @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") @Positive Long userId, @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }
}
