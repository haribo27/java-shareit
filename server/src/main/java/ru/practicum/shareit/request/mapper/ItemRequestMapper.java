package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        uses = ItemMapper.class)
public interface ItemRequestMapper {

    ItemRequest mapToItemRequest(NewItemRequestDto request);

    @Mapping(target = "items", source = "items", qualifiedByName = "mapToItemRequestDto")
    RequestDto mapToItemRequestDto(ItemRequest itemRequest);

    @Named("mapToItemRequestDto")
    @Mapping(target = "ownerId", expression = "java(getOwnerId(item.getOwner()))")
    ItemRequestDto maptoItemRequestDto(Item item);

    default long getOwnerId(User user) {
        return user.getId();
    }
}
