package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerIT {

    @Autowired
    UserController userController;

    UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                null,
                "user",
                "user@mail.com"
        );
    }

    @Test
    void testCreate() {
        UserDto testUserDto = userController.create(userDto);

        assertNotNull(testUserDto);
        assertEquals(1L, testUserDto.getId());
    }

    @Test
    void testGetOk() {
        userController.create(userDto);
        UserDto testUserDto = userController.get(1L);

        assertNotNull(testUserDto);
        assertEquals(userDto.getName(), testUserDto.getName());
    }

    @Test
    void testGetNotFound() {
        userController.create(userDto);

        assertThrows(DataNotFoundException.class, () -> userController.get(2L));
    }

    @Test
    void testGetUsers() {
        userController.create(userDto);
        List<UserDto> userDtoList = userController.getUsers();

        assertNotNull(userDtoList);
        assertEquals(1, userDtoList.size());
        assertEquals(userDto.getName(), userDtoList.get(0).getName());
    }

    @Test
    void testGetUsersEmpty() {
        List<UserDto> userDtoList = userController.getUsers();

        assertNotNull(userDtoList);
        assertEquals(0, userDtoList.size());
    }

    @Test
    void testUpdate() {
        userController.create(userDto);
        UserDto updatedUserDto = new UserDto(
                null,
                "updatedUser",
                "updatedUser@mail.com"
        );

        UserDto actualUserDto = userController.update(1L, updatedUserDto);

        assertNotNull(actualUserDto);
        assertEquals(1L, actualUserDto.getId());
        assertEquals(updatedUserDto.getName(), actualUserDto.getName());
        assertEquals(updatedUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void testUpdateOnlyName() {
        userController.create(userDto);
        UserDto savedUserDto = userController.get(1L);
        UserDto updatedUserDto = new UserDto(
                null,
                "updatedUser",
                null
        );

        UserDto actualUserDto = userController.update(1L, updatedUserDto);

        assertNotNull(actualUserDto);
        assertEquals(1L, actualUserDto.getId());
        assertEquals(updatedUserDto.getName(), actualUserDto.getName());
        assertEquals(savedUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void testUpdateOnlyMail() {
        userController.create(userDto);
        UserDto savedUserDto = userController.get(1L);
        UserDto updatedUserDto = new UserDto(
                null,
                null,
                "updated@mail.com"
        );

        UserDto actualUserDto = userController.update(1L, updatedUserDto);

        assertNotNull(actualUserDto);
        assertEquals(1L, actualUserDto.getId());
        assertEquals(savedUserDto.getName(), actualUserDto.getName());
        assertEquals(updatedUserDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void testUpdateNotFound() {
        UserDto updatedUserDto = new UserDto(
                null,
                "updatedUser",
                "updated@mail.com"
        );

        assertThrows(DataNotFoundException.class, () -> userController.update(2L, updatedUserDto));
    }

    @Test
    void testDelete() {
        userController.create(userDto);
        List<UserDto> userDtoList = userController.getUsers();

        assertFalse(userDtoList.isEmpty());

        userController.delete(1L);

        userDtoList = userController.getUsers();
        assertTrue(userDtoList.isEmpty());
    }
}