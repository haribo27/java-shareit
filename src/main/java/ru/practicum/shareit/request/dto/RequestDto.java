package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestDto {

    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemRequestDto> items;
}
