package com.example.takehome.service.impl;

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
import com.example.takehome.service.BookingService;
import com.example.takehome.service.async.BookingAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final BookingAsyncService bookingAsyncService;

    @Override
    public BookingDto createBooking(CreateBookingRequest createBookingRequest,
                                    SecurityUser securityUser) {
        validateBookingRequest(createBookingRequest);
        Hotel hotel = hotelRepository.findById(createBookingRequest.getHotelId())
                                     .orElseThrow(() -> new ServiceException(ErrorCode.HOTEL_NOT_FOUND));
        Room room = hotel.getRooms().stream().filter(r -> r.getId().equals(createBookingRequest.getRoomId()))
                         .findFirst().orElseThrow(() -> new ServiceException(ErrorCode.ROOM_NOT_FOUND));
        if (createBookingRequest.getOccupants() > room.getMaxOccupants()) {
            throw new ServiceException(ErrorCode.ROOM_MAX_OCCUPANTS_EXCEEDED);
        }
        TakehomeUser user = userRepository.findById(securityUser.getId())
                                          .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        Booking booking = bookingRepository.save(Booking.builder()
                                 .occupants(createBookingRequest.getOccupants())
                                 .status(BookingStatus.NEW)
                                 .hotel(hotel)
                                 .room(room)
                                 .bookedBy(user)
                                 .checkinDate(createBookingRequest.getCheckinDate())
                                 .checkoutDate(createBookingRequest.getCheckoutDate())
                                 .build());
        bookingAsyncService.processBookingAfterInitialSave(booking);
        return new BookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(SecurityUser securityUser) {
        return bookingRepository.findAllByUserId(securityUser.getId())
                .stream().map(BookingDto::new).toList();
    }


    private void validateBookingRequest(CreateBookingRequest createBookingRequest) {
        if (createBookingRequest.getCheckinDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ErrorCode.CHECKIN_DATE_MUST_NOT_BE_BEFORE_TODAY);
        }

        if (createBookingRequest.getCheckoutDate().isBefore(createBookingRequest.getCheckinDate())) {
            throw new ServiceException(ErrorCode.CHECKOUT_DATE_MUST_BE_AFTER_CHECKIN_DATE);
        }
    }
}
