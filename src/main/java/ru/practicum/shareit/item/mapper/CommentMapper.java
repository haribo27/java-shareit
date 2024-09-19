package ru.practicum.shareit.item.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface CommentMapper {

    Comment mapToComment(NewCommentRequestDto requestDto);

    @Mapping(target = "authorName", expression = "java(getAuthorName(comment.getAuthor()))")
    CommentDto mapToCommentDto(Comment comment);

    default String getAuthorName(User user) {
        return user.getName();
    }
}
