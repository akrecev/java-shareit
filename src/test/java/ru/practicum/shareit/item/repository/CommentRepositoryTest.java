package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    Item item;

    Comment comment;

    User author;

    User owner;

    @BeforeEach
    void beforeEach() {

        author = userRepository.save(new User(
                1L,
                "author",
                "author@mail.com"
        ));

        owner = userRepository.save(new User(
                2L,
                "owner",
                "owner@mail.com"
        ));

        item = itemRepository.save(new Item(
                1L,
                "item",
                "item description",
                true,
                owner,
                null
        ));

        comment = commentRepository.save(new Comment(
                1L,
                "comment text",
                item,
                author,
                LocalDateTime.now()
        ));
    }

    @Test
    void findAllByItemId() {
        List<Comment> commentList = commentRepository.findAllByItemId(item.getId());

        assertFalse(commentList.isEmpty());
        assertEquals(comment, commentList.get(0));
    }
}