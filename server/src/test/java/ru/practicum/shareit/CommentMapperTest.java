package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentRequestDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        commentMapper = Mappers.getMapper(CommentMapper.class);
    }

    @Test
    void mapToComment_ShouldMapNewCommentRequestDtoToComment() {
        // Arrange
        NewCommentRequestDto requestDto = new NewCommentRequestDto();
        requestDto.setText("This is a great item!");


        // Act
        Comment comment = commentMapper.mapToComment(requestDto);

        // Assert
        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("This is a great item!");
        // You might want to set other necessary fields in Comment, like author
    }

    @Test
    void mapToCommentDto_ShouldMapCommentToCommentDto() {
        // Arrange
        User author = new User();
        author.setName("User Name");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("This is a great item!");
        comment.setAuthor(author);

        // Act
        CommentDto commentDto = commentMapper.mapToCommentDto(comment);

        // Assert
        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("This is a great item!");
        assertThat(commentDto.getAuthorName()).isEqualTo("User Name");
    }

    @Test
    void getAuthorName_ShouldReturnUserName() {
        // Arrange
        User user = new User();
        user.setName("User Name");

        // Act
        String authorName = commentMapper.getAuthorName(user);

        // Assert
        assertThat(authorName).isEqualTo("User Name");
    }
}
