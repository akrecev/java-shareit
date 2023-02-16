package ru.practicum.shareit.user;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utility.Create;
import ru.practicum.shareit.utility.Update;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/{id}")
    public UserDto get(@Positive @PathVariable Long id) {
        return userService.get(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@Positive @PathVariable Long id, @Validated({Update.class}) @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {
        userService.delete(id);
    }

}
