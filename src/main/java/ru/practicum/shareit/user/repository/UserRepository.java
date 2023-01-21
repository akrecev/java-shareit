package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> find(Long id);

    List<User> findAll();

    User update(User updatedUser);

    void delete(Long id);
}
