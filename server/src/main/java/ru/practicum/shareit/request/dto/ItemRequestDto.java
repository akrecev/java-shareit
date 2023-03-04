package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class ItemRequestDto {

    private Long id;

    private String description;

    private Long requestorId;

    private LocalDateTime created;

    private List<ItemDto> items;

}
