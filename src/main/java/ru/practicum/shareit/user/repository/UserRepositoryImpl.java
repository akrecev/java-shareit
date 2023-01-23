package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long generatedId = 0;

    @Override
    public User save(User user) {
        user.setId(newId());
        users.put(user.getId(), user);

        return users.get(user.getId());
    }

    @Override
    public Optional<User> find(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User updatedUser) {
        User existingUser = users.get(updatedUser.getId());
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }

        return existingUser;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private Long newId() {
        return ++generatedId;
    }


}
