package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemRequestDto {

    @NotBlank
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
}
