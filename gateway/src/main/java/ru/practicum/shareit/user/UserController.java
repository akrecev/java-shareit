package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utility.Create;
import ru.practicum.shareit.utility.Update;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@Positive @PathVariable Long id) {
        return userClient.get(id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @Positive @PathVariable Long id, @Validated({Update.class}) @RequestBody UserDto userDto
    ) {
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {
        userClient.delete(id);
    }

}
