package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemRequestDto {

    @NotBlank
    @Size(max = 255)
    private String description;
}
