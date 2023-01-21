package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.EmailConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long generatedId = 0;

    @Override
    public User save(User user) {
        throwEmailConflict(user);
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
            throwEmailConflict(updatedUser);
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

    private void throwEmailConflict(User user) {
        boolean isRepeatEmail = users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
        if (isRepeatEmail) {
            throw new EmailConflictException("User email already registered");
        }
    }

}
