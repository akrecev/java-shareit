package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CommentDtoResponse {

    private Long id;

    private String text;

    private Item item;

    private String authorName;

    private LocalDateTime created;

}
