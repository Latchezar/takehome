package com.example.takehome.service;

import com.example.takehome.config.security.SecurityUser;
import com.example.takehome.rest.dto.BookingDto;
import com.example.takehome.rest.dto.CreateBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingRequest createBookingRequest,
                             SecurityUser securityUser);

    List<BookingDto> getUserBookings(SecurityUser securityUser);
}
