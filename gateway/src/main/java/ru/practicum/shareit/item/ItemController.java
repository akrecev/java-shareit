package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> commentCreate(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Positive @PathVariable Long itemId,
                                                @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return itemClient.commentCreate(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Positive @PathVariable Long itemId) {
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItems(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "text") String text,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        return itemClient.getSearchItems(userId, text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Positive @PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                       @Positive @PathVariable Long itemId) {
        itemClient.delete(userId, itemId);
    }
}
