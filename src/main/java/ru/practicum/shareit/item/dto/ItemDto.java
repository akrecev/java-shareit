package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    private String name;

    @NotBlank(groups = {Create.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    private Long request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return id.equals(itemDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
