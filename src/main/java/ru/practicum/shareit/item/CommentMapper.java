package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    private CommentMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static CommentDtoResponse toCommentDtoResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentDtoResponse(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                LocalDateTime.now()
        );
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                null,
                null
        );

    }

}
