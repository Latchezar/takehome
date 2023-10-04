package com.example.takehome.rest.dto;

import com.example.takehome.common.BookingStatus;
import com.example.takehome.model.Booking;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {
    private Long id;
    private Integer occupants;
    private BookingStatus status;
    private Long hotelId;
    private Long roomId;
    private Long bookedBy;
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    private LocalDateTime createdAt;

    public BookingDto(Booking booking) {
        this.id = booking.getId();
        this.occupants = booking.getOccupants();
        this.status = booking.getStatus();
        this.hotelId = booking.getHotel().getId();
        this.roomId = booking.getRoom().getId();
        this.bookedBy = booking.getBookedBy().getId();
        this.checkinDate = booking.getCheckinDate();
        this.checkoutDate = booking.getCheckoutDate();
        this.createdAt = booking.getCreatedAt();
    }
}
