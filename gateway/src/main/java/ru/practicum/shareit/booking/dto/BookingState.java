package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {

    ALL,        // все
    CURRENT,    // текущие
    PAST,       // завершённые
    FUTURE,     // будущие
    WAITING,    // ожидающие подтверждения
    REJECTED;    // отклонённые

    public static Optional<BookingState> from(String name) {
        for (BookingState value : BookingState.values()) {
            if (name.equals(value.toString())) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
