package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository requestRepository;

    User requestor;

    User noRequestor;

    ItemRequest request;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {

        requestor = userRepository.save(new User(
                1L,
                "requestor",
                "requestor@mail.com"
        ));

        noRequestor = userRepository.save(new User(
                2L,
                "noRequestor",
                "noRequestor@mail.com"
        ));

        request = requestRepository.save(new ItemRequest(
                1L,
                "request description",
                requestor,
                LocalDateTime.now().minusHours(1)
        ));

    }

    @Test
    void findAllByRequestorId() {
        Page<ItemRequest> requestPage = requestRepository.findAllByRequestorId(
                requestor.getId(), Pageable.unpaged()
        );

        assertFalse(requestPage.toList().isEmpty());
        assertEquals(request, requestPage.toList().get(0));
    }

    @Test
    void findAllByRequestorIdNot() {
        Page<ItemRequest> requestPageSecond = requestRepository.findAllByRequestorIdNot(
                noRequestor.getId(), Pageable.unpaged()
        );

        assertFalse(requestPageSecond.toList().isEmpty());
        assertEquals(request, requestPageSecond.toList().get(0));
    }

}