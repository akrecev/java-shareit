package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CommentDto {

    private Long id;

    private String text;

    private Long itemId;

    private Long authorId;

}
