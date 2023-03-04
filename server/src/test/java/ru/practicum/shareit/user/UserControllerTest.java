package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private static final String PATH = "/users";
    private static final String PATH_WITH_ID = "/users/1";

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    UserDto userDto = new UserDto(
            1L,
            "John Doe",
            "john.doe@mail.com"
    );


    @Test
    void testCreateOk() throws Exception {
        when(userService.create(userDto))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.post(PATH)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .create(userDto);
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.get(anyLong()))
                .thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.get(PATH_WITH_ID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1))
                .get(anyLong());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(Collections.singletonList(userDto));

        mvc.perform(MockMvcRequestBuilders.get(PATH)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));

        verify(userService, times(1))
                .getAll();
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto updateUser = new UserDto(
                1L,
                "Vasiliy Fortochkin",
                "vasf@mail.com");

        when(userService.update(1L, updateUser))
                .thenReturn(updateUser);

        mvc.perform(MockMvcRequestBuilders.patch(PATH_WITH_ID)
                        .content(mapper.writeValueAsString(updateUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUser.getName())))
                .andExpect(jsonPath("$.email", is(updateUser.getEmail())));

        verify(userService, times(1))
                .update(anyLong(), any(UserDto.class));
    }

    @Test
    void testDeleteUserById() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(PATH_WITH_ID))
                .andExpect(status().isOk());
    }
}