package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.ConflictException;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.utility.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User savedUser = UserMapper.toUser(userDto);
        throwEmailConflict(savedUser);

        return UserMapper.toUserDto(userRepository.save(savedUser));
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toUserDto(find(id));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        find(id);
        User updatedUser = UserMapper.toUser(userDto);
        updatedUser.setId(id);
        if (updatedUser.getEmail() != null) {
            throwEmailConflict(updatedUser);
        }

        return UserMapper.toUserDto(userRepository.update(updatedUser));
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public User find(Long id) {
        return userRepository.find(id).orElseThrow(() -> new DataNotFoundException("id:" + id));
    }

    private void throwEmailConflict(User user) {
        boolean isRepeatEmail = userRepository.findAll()
                .stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
        if (isRepeatEmail) {
            throw new ConflictException("User email already registered");
        }
    }
}
