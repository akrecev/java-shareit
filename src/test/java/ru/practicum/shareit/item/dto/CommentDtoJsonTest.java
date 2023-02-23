package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        CommentDto commentDto = new CommentDto(
                1L,
                "comment text",
                1L,
                1L
        );

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                          .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                          .isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                          .isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.authorId")
                          .isEqualTo(1);

    }

}