package ru.practicum.shareit.gateway.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdateItemRequestDto {

    private long id;
    @Size(max = 255)
    private String name;
    @Size(max = 2000)
    private String description;
    private Boolean available;
}
