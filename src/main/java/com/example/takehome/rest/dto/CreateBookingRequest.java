package com.example.takehome.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor@AllArgsConstructor
public class CreateBookingRequest {
    @NotNull(message = "Booking requires a hotel")
    private Long hotelId;
    @NotNull(message = "Booking requires a room")
    private Long roomId;
    @NotNull(message = "Booking requires occupants")
    private Integer occupants;
    @NotNull(message = "Booking requires a checkin date")
    private LocalDateTime checkinDate;
    @NotNull(message = "Booking requires a checkout date")
    private LocalDateTime checkoutDate;
}
