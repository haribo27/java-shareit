package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentRequestDto {

    @NotBlank
    @Size(max = 2000)
    private String text;
}
