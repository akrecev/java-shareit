package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                "item name",
                "item description",
                true,
                1L
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                          .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name")
                          .isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                          .isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                          .isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                          .isEqualTo(1);
    }

}