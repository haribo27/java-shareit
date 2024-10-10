package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface BookingMapper {

    Booking mapToBooking(NewBookingRequest bookingRequest);

    BookingDto toBookingDto(Booking booking);
}
