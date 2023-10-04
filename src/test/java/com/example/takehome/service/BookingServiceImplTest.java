package com.example.takehome.service;

import com.example.takehome.common.BookingStatus;
import com.example.takehome.config.security.SecurityUser;
import com.example.takehome.exceptions.ErrorCode;
import com.example.takehome.exceptions.ServiceException;
import com.example.takehome.model.Booking;
import com.example.takehome.model.Hotel;
import com.example.takehome.model.Room;
import com.example.takehome.model.TakehomeUser;
import com.example.takehome.repository.BookingRepository;
import com.example.takehome.repository.HotelRepository;
import com.example.takehome.repository.UserRepository;
import com.example.takehome.rest.dto.BookingDto;
import com.example.takehome.rest.dto.CreateBookingRequest;
import com.example.takehome.service.async.BookingAsyncService;
import com.example.takehome.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingAsyncService bookingAsyncService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private SecurityUser securityUser = new SecurityUser(11L, "username", "", List.of());

    @Test
    void createBookingShouldSucceedOnProperRequest() {
        final Long hotelId = 1L;
        final Long roomId = 2L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                                                           .hotelId(hotelId)
                                                           .checkinDate(LocalDateTime.now().plusDays(1))
                                                           .checkoutDate(LocalDateTime.now().plusDays(2))
                                                           .occupants(2)
                                                           .roomId(roomId)
                                                           .build();
        Hotel hotel = Hotel.builder()
                           .id(hotelId)
                           .rooms(List.of(Room.builder()
                                              .id(roomId)
                                              .maxOccupants(2)
                                              .details("1")
                                              .build()))
                           .build();

        TakehomeUser user = TakehomeUser.builder()
                                        .id(securityUser.getId())
                                        .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(userRepository.findById(securityUser.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        BookingDto result = bookingService.createBooking(request, securityUser);

        assertNotNull(result);
        assertEquals(request.getOccupants(), result.getOccupants());
        assertEquals(request.getRoomId(), result.getRoomId());
        assertEquals(request.getHotelId(), result.getHotelId());
        assertEquals(request.getCheckinDate(), result.getCheckinDate());
        assertEquals(request.getCheckoutDate(), result.getCheckoutDate());
        assertEquals(securityUser.getId(), result.getBookedBy());
        assertEquals(BookingStatus.NEW, result.getStatus());

        verify(bookingAsyncService, times(1)).processBookingAfterInitialSave(any(Booking.class));
    }

    @Test
    void createBookingShouldThrowWhenHotelIsNotFound() {
        final Long hotelId = 1L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                                                           .hotelId(hotelId)
                                                           .checkinDate(LocalDateTime.now().plusDays(1))
                                                           .checkoutDate(LocalDateTime.now().plusDays(2))
                                                           .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bookingService.createBooking(request, securityUser));

        assertEquals(ErrorCode.HOTEL_NOT_FOUND, exception.getError());
        verify(bookingAsyncService, times(0)).processBookingAfterInitialSave(any(Booking.class));
    }

    @Test
    void createBookingShouldThrowWhenRoomIsNotFound() {
        final Long hotelId = 1L;
        final Long roomId = 2L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                                                           .hotelId(hotelId)
                                                           .checkinDate(LocalDateTime.now().plusDays(1))
                                                           .checkoutDate(LocalDateTime.now().plusDays(2))
                                                           .roomId(roomId)
                                                           .build();
        Hotel hotel = Hotel.builder()
                           .id(hotelId)
                           .rooms(List.of())
                           .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));

        ServiceException exception = assertThrows(ServiceException.class, () -> bookingService.createBooking(request, securityUser));

        assertEquals(ErrorCode.ROOM_NOT_FOUND, exception.getError());
        verify(bookingAsyncService, times(0)).processBookingAfterInitialSave(any(Booking.class));
    }

    @Test
    void createBookingShouldThrowWhenRequestExceedsMaxOccupants() {
        final Long hotelId = 1L;
        final Long roomId = 2L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                                                           .hotelId(hotelId)
                                                           .checkinDate(LocalDateTime.now().plusDays(1))
                                                           .checkoutDate(LocalDateTime.now().plusDays(2))
                                                           .occupants(3)
                                                           .roomId(roomId)
                                                           .build();
        Hotel hotel = Hotel.builder()
                           .id(hotelId)
                           .rooms(List.of(Room.builder()
                                              .id(roomId)
                                              .maxOccupants(2)
                                              .details("1")
                                              .build()))
                           .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));

        ServiceException exception = assertThrows(ServiceException.class, () -> bookingService.createBooking(request, securityUser));

        assertEquals(ErrorCode.ROOM_MAX_OCCUPANTS_EXCEEDED, exception.getError());
        verify(bookingAsyncService, times(0)).processBookingAfterInitialSave(any(Booking.class));
    }

    @Test
    void createBookingShouldThrowWhenUserIsNotFound() {
        final Long hotelId = 1L;
        final Long roomId = 2L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                                                           .hotelId(hotelId)
                                                           .checkinDate(LocalDateTime.now().plusDays(1))
                                                           .checkoutDate(LocalDateTime.now().plusDays(2))
                                                           .occupants(2)
                                                           .roomId(roomId)
                                                           .build();
        Hotel hotel = Hotel.builder()
                           .id(hotelId)
                           .rooms(List.of(Room.builder()
                                              .id(roomId)
                                              .maxOccupants(2)
                                              .details("1")
                                              .build()))
                           .build();
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(hotel));
        when(userRepository.findById(securityUser.getId())).thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () -> bookingService.createBooking(request, securityUser));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getError());
        verify(bookingAsyncService, times(0)).processBookingAfterInitialSave(any(Booking.class));
    }

    @Test
    void createBookingShouldThrowWhenCheckinDateIsInThePast() {
        final Long hotelId = 1L;
        final Long roomId = 2L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                                                           .hotelId(hotelId)
                                                           .checkinDate(LocalDateTime.now().minusDays(1))
                                                           .checkoutDate(LocalDateTime.now().plusDays(2))
                                                           .occupants(2)
                                                           .roomId(roomId)
                                                           .build();

        ServiceException exception = assertThrows(ServiceException.class, () -> bookingService.createBooking(request, securityUser));

        assertEquals(ErrorCode.CHECKIN_DATE_MUST_NOT_BE_BEFORE_TODAY, exception.getError());
        verify(bookingAsyncService, times(0)).processBookingAfterInitialSave(any(Booking.class));
    }

    @Test
    void createBookingShouldThrowWhenCheckoutDateIsBeforeCheckinDate() {
        final Long hotelId = 1L;
        final Long roomId = 2L;
        CreateBookingRequest request = CreateBookingRequest.builder()
                                                           .hotelId(hotelId)
                                                           .checkinDate(LocalDateTime.now().plusDays(2))
                                                           .checkoutDate(LocalDateTime.now().plusDays(1))
                                                           .occupants(2)
                                                           .roomId(roomId)
                                                           .build();

        ServiceException exception = assertThrows(ServiceException.class, () -> bookingService.createBooking(request, securityUser));

        assertEquals(ErrorCode.CHECKOUT_DATE_MUST_BE_AFTER_CHECKIN_DATE, exception.getError());
        verify(bookingAsyncService, times(0)).processBookingAfterInitialSave(any(Booking.class));
    }

    @Test
    void getUserBookingsShouldReturnConvertedDtos() {
        final Long hotelId = 1L;
        final Long roomId = 2L;
        final Long bookingId = 3L;
        Room room = Room.builder()
                        .id(roomId)
                        .maxOccupants(2)
                        .details("1")
                        .build();
        TakehomeUser takehomeUser = TakehomeUser.builder()
                                                .id(securityUser.getId())
                                                .username(securityUser.getUsername())
                                                .build();
        Hotel hotel = Hotel.builder()
                           .id(hotelId)
                           .rooms(List.of(room))
                           .build();
        Booking booking = Booking.builder()
                                 .occupants(1)
                                 .status(BookingStatus.NEW)
                                 .hotel(hotel)
                                 .room(room)
                                 .bookedBy(takehomeUser)
                                 .checkinDate(LocalDateTime.now().plusDays(1))
                                 .checkoutDate(LocalDateTime.now().plusDays(2))
                                 .build();
        when(bookingRepository.findAllByUserId(securityUser.getId()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.getUserBookings(securityUser);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());

        BookingDto dto = bookings.get(0);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getOccupants(), dto.getOccupants());
        assertEquals(room.getId(), dto.getRoomId());
        assertEquals(hotel.getId(), dto.getHotelId());
        assertEquals(booking.getCheckinDate(), dto.getCheckinDate());
        assertEquals(booking.getCheckoutDate(), dto.getCheckoutDate());
        assertEquals(securityUser.getId(), dto.getBookedBy());
        assertEquals(BookingStatus.NEW, dto.getStatus());
    }
}
