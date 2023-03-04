package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository requestRepository;

    User requestor;

    User owner;

    ItemRequest request;

    Item item;

    @BeforeEach
    void beforeEach() {

        requestor = userRepository.save(new User(
                1L,
                "requestor",
                "requestor@mail.com"
        ));

        owner = userRepository.save(new User(
                2L,
                "owner",
                "owner@mail.com"
        ));

        request = requestRepository.save(new ItemRequest(
                1L,
                "request description",
                requestor,
                LocalDateTime.now().minusHours(1)
        ));

        item = itemRepository.save(new Item(
                1L,
                "item",
                "item description",
                true,
                owner,
                request
        ));
    }


    @Test
    void findAllByOwnerIdOrderById() {
        Page<Item> itemPage = itemRepository.findAllByOwnerIdOrderById(owner.getId(), Pageable.unpaged());

        assertFalse(itemPage.toList().isEmpty());
        assertEquals(item, itemPage.toList().get(0));
    }

    @Test
    void findAllByRequestIdOrderById() {
        List<Item> itemList = itemRepository.findAllByRequestIdOrderById(request.getId());

        assertFalse(itemList.isEmpty());
        assertEquals(item, itemList.get(0));
    }

    @Test
    void searchOk() {
        Page<Item> itemPage = itemRepository.search("item", Pageable.unpaged());

        assertFalse(itemPage.toList().isEmpty());
        assertEquals(item, itemPage.toList().get(0));
    }

    @Test
    void searchEmpty() {
        Page<Item> itemPage = itemRepository.search("empty", Pageable.unpaged());

        assertTrue(itemPage.toList().isEmpty());
    }
}