package ru.practicum.shareit.exception.model;

public class MissingUserInRequestException extends RuntimeException {
    public MissingUserInRequestException(String message) {
        super(message);
    }
}
