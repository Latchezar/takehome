package com.example.takehome.rest.controller;

import com.example.takehome.config.security.SecurityUser;
import com.example.takehome.rest.dto.BookingDto;
import com.example.takehome.rest.dto.CreateBookingRequest;
import com.example.takehome.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingsController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody CreateBookingRequest createBookingRequest,
                                    @AuthenticationPrincipal SecurityUser securityUser) {
        return bookingService.createBooking(createBookingRequest, securityUser);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@AuthenticationPrincipal SecurityUser securityUser) {
        return bookingService.getUserBookings(securityUser);
    }
}
