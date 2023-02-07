package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.sevice.BookingService;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.utility.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @Validated({Create.class}) @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse get(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                  @PathVariable @Positive Long bookingId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getByBooker(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                @RequestParam(value = "state", defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));

        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                               @RequestParam(value = "state", defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));

        return bookingService.getByOwner(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse confirm(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                      @PathVariable @Positive Long bookingId,
                                      @RequestParam(value = "approved") @NotNull Boolean approved) {
        return bookingService.confirm(userId, bookingId, approved);
    }

}

