package ru.practicum.shareit.booking.validate;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getStart().isBefore(bookingDto.getEnd());
    }

    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
