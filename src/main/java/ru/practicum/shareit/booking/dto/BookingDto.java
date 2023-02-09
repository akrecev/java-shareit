package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.validate.StartBeforeEnd;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@StartBeforeEnd(groups = {Create.class})
public class BookingDto {

    private Long id;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @Future(groups = {Create.class})
    private LocalDateTime end;

    @NotNull(groups = {Create.class})
    private Long itemId;

    private Long bookerId;

    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
