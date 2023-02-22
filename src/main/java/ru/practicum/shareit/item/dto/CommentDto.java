package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CommentDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    private String text;

    private Long itemId;

    private Long authorId;

}
