package ru.practicum.shareit.gateway.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewRequestDto {

    @Size(max = 255)
    private String description;
}
