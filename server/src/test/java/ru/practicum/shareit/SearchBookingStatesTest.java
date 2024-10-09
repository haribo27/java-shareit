package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.SearchBookingStates;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchBookingStatesTest {

    @Test
    void allStates_ShouldHaveCorrectNames() {
        // Проверяем, что все перечисленные состояния имеют правильные имена.
        assertEquals("ALL", SearchBookingStates.ALL.name());
        assertEquals("CURRENT", SearchBookingStates.CURRENT.name());
        assertEquals("PAST", SearchBookingStates.PAST.name());
        assertEquals("FUTURE", SearchBookingStates.FUTURE.name());
        assertEquals("WAITING", SearchBookingStates.WAITING.name());
        assertEquals("REJECTED", SearchBookingStates.REJECTED.name());
    }

    @Test
    void allStates_ShouldHaveCorrectOrdinalValues() {
        // Проверяем, что порядковые значения всех состояний верны.
        assertEquals(0, SearchBookingStates.ALL.ordinal());
        assertEquals(1, SearchBookingStates.CURRENT.ordinal());
        assertEquals(2, SearchBookingStates.PAST.ordinal());
        assertEquals(3, SearchBookingStates.FUTURE.ordinal());
        assertEquals(4, SearchBookingStates.WAITING.ordinal());
        assertEquals(5, SearchBookingStates.REJECTED.ordinal());
    }
}
