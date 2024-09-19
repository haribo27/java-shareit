package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewCommentRequestDto {

    private long id;
    @NotBlank
    private String text;
}
