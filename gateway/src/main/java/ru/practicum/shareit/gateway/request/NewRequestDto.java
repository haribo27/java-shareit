package ru.practicum.shareit.gateway.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewRequestDto {

    @NotBlank
    @Size(max = 255)
    private String description;
}
