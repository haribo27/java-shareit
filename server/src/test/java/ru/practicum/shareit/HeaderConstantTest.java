package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.util.HeaderConstant;

import static org.assertj.core.api.Assertions.assertThat;

class HeaderConstantTest {

    @Test
    void testUserIdHeader() {
        // Проверка, что константа USER_ID_HEADER инициализирована правильно
        assertThat(HeaderConstant.USER_ID_HEADER).isEqualTo("X-Sharer-User-Id");
    }
}
