package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Captor
    ArgumentCaptor<User> argumentCaptor;

    long userId = 1L;

    User user = new User(
            userId,
            "John Doe",
            "john.doe@mail.com"
    );

    UserDto userDto = new UserDto(
            1L,
            "John Doe",
            "john.doe@mail.com"
    );

    @Test
    void testCreateUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto actualUserDto = userService.create(userDto);

        assertNotNull(actualUserDto);
        assertEquals(userDto.getName(), actualUserDto.getName());
        assertEquals(userDto.getEmail(), actualUserDto.getEmail());
        verify(userRepository, times(1))
                .save(any());
    }

    @Test
    void testGetUserByIdOk() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        UserDto actualUserDto = userService.get(userId);

        assertNotNull(actualUserDto);
        assertEquals(userDto.getName(), actualUserDto.getName());
        assertEquals(userDto.getEmail(), actualUserDto.getEmail());
        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void testGetUserByNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.get(userId));

        assertEquals("id:1", exception.getMessage());
        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void testGetAllUsersPage() {
        Page<User> pageUsers = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(PageRequest.ofSize(10)))
                .thenReturn(pageUsers);

        List<UserDto> actualUserDtoList = userService.getAll();

        assertEquals(userDto.getName(), actualUserDtoList.get(0).getName());
        assertEquals(userDto.getEmail(), actualUserDtoList.get(0).getEmail());
        verify(userRepository, times(1))
                .findAll(PageRequest.ofSize(10));
    }

    @Test
    void testUpdateOk() {
        UserDto updatedUserDto = new UserDto(
                userId,
                "Vasiliy Fortochkin",
                "vasf@mail.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(UserMapper.toUser(updatedUserDto))).thenReturn(UserMapper.toUser(updatedUserDto));

        userService.update(userId, updatedUserDto);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals(updatedUserDto.getName(), savedUser.getName());
        assertEquals(updatedUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void testUpdateOnlyName() {
        UserDto updatedUserDto = new UserDto(
                userId,
                "Vasiliy Fortochkin",
                user.getEmail()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(UserMapper.toUser(updatedUserDto))).thenReturn(UserMapper.toUser(updatedUserDto));

        userService.update(userId, updatedUserDto);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals(updatedUserDto.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void testUpdateOnlyMail() {
        UserDto updatedUserDto = new UserDto(
                userId,
                user.getName(),
                "update@mail.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(UserMapper.toUser(updatedUserDto))).thenReturn(UserMapper.toUser(updatedUserDto));

        userService.update(userId, updatedUserDto);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals(user.getName(), savedUser.getName());
        assertEquals(updatedUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void testUpdateNameIsNull() {
        UserDto updatedUserDto = new UserDto(
                userId,
                null,
                "update@mail.com"
        );

        UserDto expectedUserDto = new UserDto(
                userId,
                "update name",
                "update@mail.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(UserMapper.toUser(expectedUserDto));

        userService.update(userId, updatedUserDto);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals(user.getName(), savedUser.getName());
        assertEquals(updatedUserDto.getEmail(), savedUser.getEmail());
    }

    @Test
    void testUpdateMailIsNull() {
        UserDto updatedUserDto = new UserDto(
                userId,
                "update name",
                null
        );

        UserDto expectedUserDto = new UserDto(
                userId,
                "update name",
                "update@mail.com"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any())).thenReturn(UserMapper.toUser(expectedUserDto));

        userService.update(userId, updatedUserDto);

        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();

        assertEquals(updatedUserDto.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void testUpdateByNotFound() {
        UserDto updatedUserDto = new UserDto(
                userId,
                "Vasiliy Fortochkin",
                "vasf@mail.com"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class,
                () -> userService.update(userId, updatedUserDto));

        assertEquals("id:1", exception.getMessage());
        verify(userRepository, never())
                .save(any());
    }


    @Test
    void delete() {
        userService.delete(userId);

        verify(userRepository, times(1))
                .deleteById(userId);
    }
}