package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.validate.StartBeforeEnd;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@StartBeforeEnd(groups = {Create.class})
@Builder
@EqualsAndHashCode
public class BookingDto {

    private Long id;

    @FutureOrPresent(groups = {Create.class})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @Future(groups = {Create.class})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    @NotNull(groups = {Create.class})
    private Long itemId;

    private Long bookerId;

    private Status status;

}
